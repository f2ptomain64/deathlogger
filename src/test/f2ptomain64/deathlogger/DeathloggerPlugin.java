package f2ptomain64.deathlogger;
import com.google.inject.Provides;
import lombok.Getter;
import net.runelite.api.*;
import net.runelite.api.events.ActorDeath;
import net.runelite.api.events.VarbitChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import javax.imageio.ImageIO;
import javax.inject.Inject;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.ui.DrawManager;
import net.runelite.client.util.ImageCapture;
import net.runelite.client.util.ImageUploadStyle;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Consumer;

@PluginDescriptor(
        name = "Death Logger",
        description = "Takes a screenshot of deaths during Theater of Blood and Chambers of Xeric. Also supports discord webhook integration.",
        tags = {"death", "raid", "raids", "shame", "tob", "theater", "discord", "discord", "webhook" "Death logger"},
        loadWhenOutdated = true,
        enabledByDefault = false
)
public class DeathloggerPlugin extends Plugin{

    @Inject
    private Client client;

    @Inject
    private ImageCapture imageCapture;

    @Inject
    private DrawManager drawManager;

    @Inject
    private ScheduledExecutorService executor;

    @Inject
    private DeathloggerConfig config;

    @Getter
    private boolean inTob;
    private boolean inRaid;
	
    @Provides
    DeathloggerConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(DeathloggerConfig.class);
    }

    @Subscribe
    public void onActorDeath(ActorDeath actorDeath)
    {
        Actor actor = actorDeath.getActor();
        if (actor instanceof Player)
        {
            Player player = (Player) actor;
            if (player != client.getLocalPlayer() && inTob)
            {
                takeScreenshot("Death of " + player.getName(), "Wall of Shame");
            }
            else {
                if (player != client.getLocalPlayer() && inRaid)
            }
        }
    }

    @Subscribe
    public void onVarbitChanged(VarbitChanged event)
    {
        inTob = client.getVar(Varbits.THEATRE_OF_BLOOD) > 1;
		inRaid = client.getVar(Varbits.IN_RAID) > 1;
    }

    private void takeScreenshot(String fileName, String subDir)
    {
        Consumer<Image> imageCallback = (img) ->
        {
            executor.submit(() -> {
                try {
                    takeScreenshot(fileName, subDir, img);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        };
        drawManager.requestNextFrameListener(imageCallback);
    }

    private void takeScreenshot(String fileName, String subDir, Image image) throws IOException
    {
        BufferedImage screenshot = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics graphics = screenshot.getGraphics();
        int gameOffsetX = 0;
        int gameOffsetY = 0;
        graphics.drawImage(image, gameOffsetX, gameOffsetY, null);
        imageCapture.takeScreenshot(screenshot, fileName, subDir, false, ImageUploadStyle.NEITHER);
        ByteArrayOutputStream screenshotOutput = new ByteArrayOutputStream();
        ImageIO.write(screenshot, "png", screenshotOutput);

        if (config.webhookEnabled() && !config.webhookLink().equals(""))
        {
            DiscordWebhook FileSender = new DiscordWebhook();
            FileSender.SendWebhook(screenshotOutput, fileName, config.webhookLink());
        }
    }
}
