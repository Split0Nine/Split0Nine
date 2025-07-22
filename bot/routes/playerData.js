const express = require('express');
const fs = require('fs');
const fetch = require('node-fetch');
const router = express.Router();

const codesPath = './data/codes.json';
const playersPath = './data/players.json';
const historyPath = './data/playerHistory.json';

router.post('/player-data', (req, res) => {
  const data = req.body;
  const { discordId, uuid, name, coloredName, rank, rankColor, hours, displayName } = data;

  if (!discordId || !uuid || !name || !coloredName || !rank || !rankColor || hours === undefined) {
    return res.status(400).json({ error: "Missing required fields" });
  }

  const players = fs.existsSync(playersPath) ? JSON.parse(fs.readFileSync(playersPath)) : {};
  const history = fs.existsSync(historyPath) ? JSON.parse(fs.readFileSync(historyPath)) : {};

  const existingDiscordLink = Object.values(players).find(p => p.discordId === discordId);
  if (existingDiscordLink) {
    return res.status(400).json({ error: "Discord account already linked to another player" });
  }

  const existingUuidLink = Object.values(players).find(p => p.uuid === uuid);
  if (existingUuidLink) {
    return res.status(400).json({ error: "Minecraft account already linked to another Discord account" });
  }

  const currentNameKey = name.toLowerCase();
  
  players[currentNameKey] = {
    uuid,
    currentName: name,
    coloredName,
    discordId,
    rank,
    rankColor,
    hours,
    displayName: displayName || rank,
    linkedAt: Date.now()
  };

  if (!history[uuid]) {
    history[uuid] = {
      names: [currentNameKey],
      firstLinked: Date.now()
    };
  }

  fs.writeFileSync(playersPath, JSON.stringify(players, null, 2));
  fs.writeFileSync(historyPath, JSON.stringify(history, null, 2));

  return res.json({ 
    success: true, 
    message: "Account linked successfully",
    playerName: name,
    rank: displayName || rank
  });
});

router.post('/player-live-data', (req, res) => {
  const { uuid } = req.body;
  if (!uuid) {
    return res.status(400).json({ error: "UUID is required" });
  }

  const players = fs.existsSync(playersPath) ? JSON.parse(fs.readFileSync(playersPath)) : {};
  const player = Object.values(players).find(p => p.uuid === uuid);

  if (!player) {
    return res.status(404).json({ error: "Player not found" });
  }

  return res.json({
    uuid: player.uuid,
    name: player.currentName,
    coloredName: player.coloredName,
    rank: player.rank,
    rankColor: player.rankColor,
    hours: player.hours,
    displayName: player.displayName || player.rank
  });
});

router.post('/get-live-player-data', async (req, res) => {
  const { uuid } = req.body;
  
  if (!uuid) {
    return res.status(400).json({ error: "UUID is required" });
  }

  try {
    const minecraftResponse = await fetch('http://ip:8080/player-data', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({ uuid }),
      timeout: 5000
    });

    if (!minecraftResponse.ok) {
      throw new Error(`Server responded with ${minecraftResponse.status}`);
    }

    const liveData = await minecraftResponse.json();
    
    const players = fs.existsSync(playersPath) ? JSON.parse(fs.readFileSync(playersPath)) : {};
    
    const playerKey = Object.keys(players).find(key => players[key].uuid === uuid);
    
    if (playerKey) {
      const existingPlayer = players[playerKey];
      
      players[playerKey] = {
        uuid: liveData.uuid,
        currentName: liveData.name,
        coloredName: liveData.coloredName,
        rank: liveData.rank,
        rankColor: liveData.rankColor,
        hours: liveData.hours,
        displayName: liveData.displayName,
        discordId: existingPlayer.discordId, 
        linkedAt: existingPlayer.linkedAt   
      };

      fs.writeFileSync(playersPath, JSON.stringify(players, null, 2));
      
      console.log(`✅ Updated player data for ${liveData.name}`);
    }

    return res.json({
      success: true,
      data: liveData,
      source: 'live'
    });

  } catch (error) {
    console.warn(`⚠️ Failed to get live data for UUID ${uuid}:`, error.message);
    
    const players = fs.existsSync(playersPath) ? JSON.parse(fs.readFileSync(playersPath)) : {};
    const player = Object.values(players).find(p => p.uuid === uuid);

    if (player) {
      return res.json({
        success: true,
        data: {
          uuid: player.uuid,
          name: player.currentName,
          coloredName: player.coloredName,
          rank: player.rank,
          rankColor: player.rankColor,
          hours: player.hours,
          displayName: player.displayName
        },
        source: 'cached'
      });
    } else {
      return res.status(404).json({ error: "Player not found" });
    }
  }
});

module.exports = router;