package com.lenis0012.bukkit.marriage2.commands;

import com.lenis0012.bukkit.marriage2.Marriage;
import com.lenis0012.bukkit.marriage2.MData;
import com.lenis0012.bukkit.marriage2.MPlayer;
import com.lenis0012.bukkit.marriage2.config.Message;
import com.lenis0012.bukkit.marriage2.config.Settings;
import com.lenis0012.bukkit.marriage2.misc.Cooldown;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class CommandPhrase extends Command {
    private final Random random = new Random();
    private final Cooldown<String> cooldown = new Cooldown<>(1, TimeUnit.MINUTES);

    public CommandPhrase(Marriage marriage) {
        super(marriage, "phrase");
        setDescription(Message.COMMAND_PHRASE.toString());
    }

    @Override
    public void execute() {

        Player player = (Player) sender;

        if(!cooldown.performCheck(player.getName())) {
            reply(Message.COOLDOWN_WAIT);
            return;
        }

        MPlayer mPlayer = marriage.getMPlayer(player);

        if(!mPlayer.isMarried()) {
            reply(Message.NOT_MARRIED);
            return;
        }

        MData marriageData = mPlayer.getMarriage();
        UUID partnerUuid = marriageData.getOtherPlayer(player.getUniqueId());
        Player partner = Bukkit.getPlayer(partnerUuid);

        if(partner == null || !partner.isOnline()) {
            reply(Message.PARTNER_NOT_ONLINE);
            return;
        }

        List<String> phrases = Settings.PHRASES.value();
        if(phrases.isEmpty()) {
            reply(Message.NO_PHRASES_CONFIGURED);
            return;
        }

        String randomPhrase = phrases.get(random.nextInt(phrases.size()));

        partner.sendMessage(Message.PHRASE_RECEIVED.toString()
                .replace("{phrase}", randomPhrase));

        player.sendMessage(Message.PHRASE_SENT.toString()
                .replace("{phrase}", randomPhrase));
    }
}