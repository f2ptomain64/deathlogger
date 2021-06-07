package f2ptomain64.deathlogger;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;


@ConfigGroup("deathlogger")
public interface DeathloggerConfig extends Config
{
    @ConfigItem(
            keyName = "webhookEnabled",
            name = "Discord Webhook",
            description = "Allows you to send death photos automatically to a discord webhook.",
            position = 1
    )
    default boolean webhookEnabled()
    {
        return false;
    }

    @ConfigItem(
            position = 2,
            keyName = "webhookLink",
            name = "Webhook URL",
            description = "Put your webhook link here, the full thing copied from discord."
    )
    default String webhookLink()
    {
        return "";
    }
}

