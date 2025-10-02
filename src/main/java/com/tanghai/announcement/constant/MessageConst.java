package com.tanghai.announcement.constant;

import java.util.Random;

public class MessageConst {
    static String[] goldTraderQuotes = {
            "Gold: Because paper money gets old, but bling lasts forever. 💰✨",
            "Buy gold, they said. It'll be fun, they said. Now I'm checking charts at 3 AM. 🕒📈",
            "Gold traders have trust issues… with everything except their shiny metal. 🏆🤔",
            "When in doubt, zoom out. Unless it’s gold, then panic a little. 😅📉",
            "I trade gold like I trade coffee: obsessively and without sleep. ☕💛",
            "Gold: the only metal that appreciates your panic. 😨💎",
            "Some collect stamps, I collect gold price swings. 📬📈💰",
            "Gold never sleeps… and neither do I during a dip. 🛌⏳",
            "In gold we trust… until the market tells us otherwise. 🙏📊",
            "Buying gold is easy, holding it without staring at charts is the real challenge. 👀💛",
            "Gold: the metal that laughs at inflation. 😂📈",
            "I whisper sweet nothings to my gold charts at night. 🌙💛",
            "Gold: cheaper than therapy, shinier than your mood. 🛋️✨",
            "When gold moves, my heart moves faster. 💓📈",
            "Gold traders: part investor, part emotional acrobat. 🤹💰",
            "I don’t chase gold, I stare until it moves. 👁️💎",
            "Gold is like a cat: unpredictable, shiny, and loves attention. 🐱✨",
            "Sleep is for fiat traders. Gold traders dream in karats. 😴💛",
            "Gold: turning caffeine into obsession since forever. ☕💰",
            "Charts don’t lie, but gold likes to tease. 😏📊",
            "I speak fluent gold: uptrend, downtrend, panic, repeat. 🔄💎",
            "Gold traders’ cardio: running to the charts every 5 minutes. 🏃📈",
            "If gold had feelings, mine would be exhausted. 😵💛",
            "Gold is proof that shiny things cause the most stress. 😅✨",
            "I invest in gold because it’s the metal version of a safety blanket. 🛡️💎",
            "Gold: turning sleepless nights into potential wealth. 🌌💰",
            "Don’t worry about silver, gold is the drama queen. 👑📉",
            "Gold traders don’t cry, they analyze candles. 🕯️📊",
            "Every gold dip is a personal heart attack. 💔📈",
            "Gold: because who doesn’t want a glittering panic button? 🔘💛",
            "I measure success in ounces of gold, not smiles. ⚖️💎",
            "Gold trading: 90% stress, 10% sparkle. ✨😬",
            "Keep calm and blame the gold market. 😌📉",
            "Gold is cheaper than heartbreak, more painful than caffeine. 💔☕💛",
            "I don’t hoard gold, I lovingly obsess over it. 💖💎",
            "Gold: turning emotions into decimals. 😢📊",
            "Some meditate, I check gold prices. 🧘💰",
            "Gold traders’ mantra: buy low, panic high, repeat. 🔁💎",
            "If you think love is volatile, try gold charts. 💔📈",
            "Gold: the only asset that makes your heart race and your wallet sweat. 🏃‍♂️💰",
            "I dream in gold candlesticks and Fibonacci retracements. 🌙📊",
            "Gold: shiny, mysterious, and occasionally traumatizing. 🪄💛",
            "Trading gold is cheaper than therapy, but less relaxing. 🛋️📈",
            "My portfolio sparkles… literally, thanks to gold. ✨💎",
            "Gold traders have a love-hate relationship with Mondays. 😍📉",
            "Gold: the asset that turns panic into poetry. 📝💛",
            "I trust gold more than my alarm clock. ⏰💰",
            "Gold charts: the only rollercoaster I willingly ride. 🎢📊",
            "Gold: proof that humans worship shiny things and stress over them. 😅✨",
            "I check gold prices more than my messages. 📱💎",
            "Gold is my cardio, my meditation, and my caffeine all in one. 🏃☕🧘💛",
            "Nothing haunts me like the last gold dip. 👻📉",
            "Gold traders don’t count sheep, they count ounces. 🐑⚖️💎",
            "Buy gold, they said. It’ll be fun, they said. Panic included. 😅💰",
            "Gold: shiny, volatile, and absolutely mesmerizing. ✨📈",
            "I talk to my gold portfolio more than people. 🗣️💎",
            "Gold: the only metal that makes panic feel profitable. 😬💛"
    };

    private static final Random RANDOM = new Random();

    /**
     * Get a random gold trader quote
     */
    public static String getRandomQuote() {
        int index = RANDOM.nextInt(goldTraderQuotes.length);
        return goldTraderQuotes[index];
    }
}
