const { Client, GatewayIntentBits, REST, Routes, SlashCommandBuilder } = require('discord.js');
const fetch = require('node-fetch');
const config = require('./config.json');
const express = require('express');
const app = express();
const fs = require('fs');
const playersPath = './data/players.json';
const playerDataRouter = require('./routes/playerData');

const client = new Client({ intents: [GatewayIntentBits.Guilds] });

app.use(express.json());

app.use('/', playerDataRouter);

const PORT = 3000;
app.listen(PORT, '45.88.9.243', () => {
  console.log(`✅ Server is running on port ${PORT}`);
});




 
const colorMap = {
  '§0': 0x000000, '§1': 0x0000AA, '§2': 0x00AA00, '§3': 0x00AAAA,
  '§4': 0xAA0000, '§5': 0xAA00AA, '§6': 0xFFAA00, '§7': 0xAAAAAA,
  '§8': 0x555555, '§9': 0x5555FF, '§a': 0x55FF55, '§b': 0x55FFFF,
  '§c': 0xFF5555, '§d': 0xFF55FF, '§e': 0xFFFF55, '§f': 0xFFFFFF
};

function initializeDataFiles() {
  const dataDir = './data';
  if (!fs.existsSync(dataDir)) {
    fs.mkdirSync(dataDir, { recursive: true });
  }
  
  const files = ['codes.json', 'players.json', 'playerHistory.json'];
  files.forEach(file => {
    const filePath = `${dataDir}/${file}`;
    if (!fs.existsSync(filePath)) {
      fs.writeFileSync(filePath, '{}');
    }
  });
}

initializeDataFiles();

client.once('ready', async () => {
  console.log(`Logged in as ${client.user.tag}`);

  const commands = [
    new SlashCommandBuilder()
      .setName('link')
      .setDescription('Link your Discord account to Minecraft'),
    new SlashCommandBuilder()
      .setName('unlink')
      .setDescription('Unlink your Minecraft account from Discord'),
    new SlashCommandBuilder()
      .setName('check')
      .setDescription('Check player info')
      .addStringOption(option =>
        option.setName('player')
          .setDescription('Player name or previous name')
          .setRequired(true))
  ];

  const rest = new REST({ version: '10' }).setToken(config.token);
  await rest.put(
    Routes.applicationGuildCommands(config.clientId, config.guildId),
    { body: commands }
  );
});

client.on('interactionCreate', async interaction => {
  if (!interaction.isChatInputCommand()) return;

  const codesPath = './data/codes.json';
  const playersPath = './data/players.json';
  const historyPath = './data/playerHistory.json';

  if (interaction.commandName === 'link') {
    const players = fs.existsSync(playersPath) ? JSON.parse(fs.readFileSync(playersPath)) : {};
    const existingPlayer = Object.values(players).find(p => p.discordId === interaction.user.id);
    
    if (existingPlayer) {
      return interaction.reply({
        content: `🚫 Your Discord account is already linked to **${existingPlayer.currentName}**.\nUse \`/unlink\` first to change accounts.`,
        flags: 64
      });
    }

    const discordId = interaction.user.id;
    const timestamp = Date.now();
    const codes = fs.existsSync(codesPath) ? JSON.parse(fs.readFileSync(codesPath)) : {};

    Object.keys(codes).forEach(code => {
      if (timestamp - codes[code].timestamp > 300000) { 
        delete codes[code];
      }
    });

    codes[discordId] = {
      discordId: discordId,
      timestamp: timestamp
    };

    fs.writeFileSync(codesPath, JSON.stringify(codes, null, 2));

    await interaction.reply({
      content: `🔗 Send this command in Minecraft:
\`/discord link ${discordId}\`

⏳ This code expires in 5 minutes.`,
      flags: 64
    });
  }

  if (interaction.commandName === 'unlink') {
    const players = fs.existsSync(playersPath) ? JSON.parse(fs.readFileSync(playersPath)) : {};
    const playerToUnlink = Object.entries(players).find(([_, player]) => player.discordId === interaction.user.id);

    if (!playerToUnlink) {
      return interaction.reply({
        content: '🚫 You don\'t have any linked Minecraft account.',
        flags: 64
      });
    }

    const [playerKey, playerData] = playerToUnlink;
    delete players[playerKey];

    const history = fs.existsSync(historyPath) ? JSON.parse(fs.readFileSync(historyPath)) : {};
    Object.keys(players).forEach(key => {
      if (players[key].uuid === playerData.uuid) {
        delete players[key];
      }
    });

    fs.writeFileSync(playersPath, JSON.stringify(players, null, 2));

    await interaction.reply({
      content: `✅ Your Minecraft account **${playerData.currentName}** has been unlinked from Discord.`,
      flags: 64
    });
  }

  if (interaction.commandName === 'check') {
    const searchName = interaction.options.getString('player').toLowerCase();
    const players = fs.existsSync(playersPath) ? JSON.parse(fs.readFileSync(playersPath)) : {};
    const history = fs.existsSync(historyPath) ? JSON.parse(fs.readFileSync(historyPath)) : {};

    let player = null;
    let foundKey = null;

    if (players[searchName]) {
      player = players[searchName];
      foundKey = searchName;
    } else {
      for (const [uuid, playerHistory] of Object.entries(history)) {
        if (playerHistory.names.some(name => name.toLowerCase() === searchName)) {
          foundKey = Object.keys(players).find(key => players[key].uuid === uuid);
          if (foundKey) {
            player = players[foundKey];
            break;
          }
        }
      }
    }

    if (!player) {
      return interaction.reply({
        content: `🚫 Player \`${searchName}\` is not linked or doesn't exist.`,
        flags: 64
      });
    }

    let dataSource = 'cached';
    try {
      const response = await fetch('http://ip:port/get-live-player-data', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ uuid: player.uuid }),
        timeout: 5000
      });

      if (response.ok) {
        const result = await response.json();
        
        if (result.success) {
          player = {
            uuid: result.data.uuid,
            currentName: result.data.name,
            coloredName: result.data.coloredName,
            rank: result.data.rank,
            rankColor: result.data.rankColor,
            hours: result.data.hours,
            displayName: result.data.displayName,
            discordId: player.discordId, 
            linkedAt: player.linkedAt   
          };

          const updatedPlayers = fs.existsSync(playersPath) ? JSON.parse(fs.readFileSync(playersPath)) : {};
          const updatedPlayer = Object.values(updatedPlayers).find(p => p.uuid === player.uuid);
          if (updatedPlayer) {
            player = updatedPlayer;
          }

          dataSource = result.source;
          console.log(`✅ Updated player data for ${player.currentName} (source: ${dataSource})`);
        }
      }
    } catch (error) {
      console.warn('⚠️ Failed to fetch live data, using cached info:', error.message);
    }

    const embedColor = colorMap[player.rankColor] ?? 0x000000;
    const skinUrl = `https://visage.surgeplay.com/full/512/${player.uuid}`;
    const headUrl = `https://visage.surgeplay.com/head/128/${player.uuid}`;

    let previousNames = '';
    if (history[player.uuid] && history[player.uuid].names.length > 0) {
      const names = history[player.uuid].names.filter(name => name !== player.currentName.toLowerCase());
      if (names.length > 0) {
        previousNames = `\n**Previous Names:** \`${names.join(', ')}\``;
      }
    }

    const statusEmoji = dataSource === 'live' ? '🟢' : '🟡';
    const statusText = dataSource === 'live' ? 'Live Data' : 'Cached Data';

    await interaction.reply({
      embeds: [
        {
          color: embedColor,
          title: player.currentName,
          description: `**Rank:** \`${player.displayName || player.rank}\`\n**Playtime Hours:** \`${player.hours}\`${previousNames}`,
          thumbnail: { url: headUrl },
          image: { url: skinUrl },
          footer: { 
            text: `${statusEmoji} • Player info`,
            icon_url: headUrl 
          }
        }
      ],
      flags: 64
    });
  }
});

client.login(config.token);