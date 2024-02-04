package net.kore.modules.misc;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import net.kore.Kore;
import net.kore.events.GuiChatEvent;
import net.kore.modules.Module;
import net.kore.settings.BooleanSetting;
import net.kore.settings.ModeSetting;
import net.kore.settings.NumberSetting;
import net.kore.ui.hud.DraggableComponent;
import net.kore.ui.hud.impl.GuesserHud;
import net.kore.utils.Notification;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import java.util.ArrayList;

public class BuildGuesser extends Module
{
    public static ModeSetting defaultPosition;
    public NumberSetting x;
    public NumberSetting y;
    public NumberSetting displayedGuesses;
    public ModeSetting blurStrength;
    public BooleanSetting autoGuess;
    public BooleanSetting autoJoin;

    private ArrayList<String> wordList;
    private int tips;
    private String tip;
    private ArrayList<String> matchingWords;
    private Thread guesses;
    private int period;
    private long lastGuess;

    public BuildGuesser() {
        super("Build Guesser", 0, Category.MISC);
        defaultPosition = new ModeSetting("Default Position", "Middle Left", new String[] { "Middle Left", "Custom"});
        this.x = new NumberSetting("guesserX", 0.0, -100000.0, 100000.0, 1.0E-5, a -> true);
        this.y = new NumberSetting("guesserY", 0.0, -100000.0, 100000.0, 1.0E-5, a -> true);
        this.blurStrength = new ModeSetting("Blur Strength", "Low", new String[] { "None", "Low", "High" });
        this.displayedGuesses = new NumberSetting("Displayed guesses", 10.0, 5, 50.0, 1.0);
        this.autoGuess = new BooleanSetting("Auto Guess", true);
        this.autoJoin = new BooleanSetting("Auto Join", true);
        this.addSettings(defaultPosition, this.x, this.y, this.blurStrength, this.displayedGuesses, this.autoGuess, this.autoJoin);

        this.wordList = new ArrayList<String>();
        this.tips = 0;
        this.guesses = null;
        this.period = 3200;
        this.lastGuess = 0L;
    }

    @Override
    public void assign()
    {
        Kore.buildGuesser = this;
    }

    @SubscribeEvent
    public void onChat(final ClientChatReceivedEvent event) {
        if (!this.isToggled()) {
            return;
        }
        if(autoJoin.isEnabled()) {
            try {
                final ScoreObjective o = Minecraft.getMinecraft().thePlayer.getWorldScoreboard().getObjectiveInDisplaySlot(0);
                if (ChatFormatting.stripFormatting(((o == null) ? Minecraft.getMinecraft().thePlayer.getWorldScoreboard().getObjectiveInDisplaySlot(1) : o).getDisplayName()).contains("GUESS THE BUILD") && ChatFormatting.stripFormatting(event.message.getFormattedText()).startsWith("This game has been recorded")) {
                    Minecraft.getMinecraft().thePlayer.sendChatMessage("/play build_battle_guess_the_build");
                }
            }
            catch (Exception ex) {}
        }
        if (event.message.getFormattedText().startsWith("§eThe theme was") && this.guesses != null) {
            this.guesses.stop();
            this.guesses = null;
        }
        if (ChatFormatting.stripFormatting(event.message.getFormattedText()).startsWith(Minecraft.getMinecraft().thePlayer.getName() + " correctly guessed the theme!") && this.guesses != null) {
            this.guesses.stop();
            this.guesses = null;
        }
        if (event.type == 2) {
            if (event.message.getFormattedText().contains("The theme is") && event.message.getFormattedText().contains("_")) {
                if (this.wordList.isEmpty()) {
                    this.loadWords();
                }
                final int newTips = this.getTips(event.message.getFormattedText());
                if (newTips != this.tips) {
                    this.tips = newTips;
                    tip = ChatFormatting.stripFormatting(event.message.getFormattedText()).replaceFirst("The theme is ", "");
                    matchingWords = this.getMatchingWords();
                    if (matchingWords.size() == 1) {
                        Kore.notificationManager.showNotification("Found 1 matching word! Sending: §f" + matchingWords.get(0), 2000, Notification.NotificationType.INFO);
                        if (this.guesses != null) {
                            this.guesses.stop();
                            this.guesses = null;
                            final ArrayList<String> list = new ArrayList<>();
                            new Thread(() -> {
                                try {
                                    Thread.sleep(this.period - (System.currentTimeMillis() - this.lastGuess));
                                }
                                catch (Exception e) {
                                    e.printStackTrace();
                                }
                                if(autoGuess.isEnabled()) {
                                    Minecraft.getMinecraft().thePlayer.sendChatMessage("/ac " + list.get(0).toLowerCase());
                                }
                            }).start();
                            return;
                        }
                        if(autoGuess.isEnabled()) {
                            Minecraft.getMinecraft().thePlayer.sendChatMessage("/ac " + matchingWords.get(0).toLowerCase());
                        }
                    }
                    else {
                        Kore.notificationManager.showNotification(String.format("Found %s matching words!", matchingWords.size()), 1500, Notification.NotificationType.INFO);
                    }
                }
            }
            else {
                this.tips = 0;
            }
        }
    }

    @SubscribeEvent
    public void onChatEvent(final GuiChatEvent event) {
        if (!this.isToggled()) {
            return;
        }

        final DraggableComponent component = GuesserHud.guesserHud;

        if (event instanceof GuiChatEvent.MouseClicked) {
            if (component.isHovered(event.mouseX, event.mouseY)) {
                defaultPosition.setSelected("Custom");
                component.startDragging();
            }
        }
        else if (event instanceof GuiChatEvent.MouseReleased) {
            component.stopDragging();
        }
        else if (event instanceof GuiChatEvent.Closed) {
            component.stopDragging();
        }
        else if (event instanceof GuiChatEvent.DrawChatEvent) {

        }
    }

    @SubscribeEvent
    public void onRender(final RenderGameOverlayEvent.Post event) {
        if (Minecraft.getMinecraft().theWorld != null && Minecraft.getMinecraft().thePlayer != null && this.isToggled() && event.type == RenderGameOverlayEvent.ElementType.ALL) {
            GuesserHud.guesserHud.drawScreen(matchingWords);
        }
    }

    private int getTips(final String text) {
        return text.replaceAll(" ", "").replaceAll("_", "").length();
    }

    private void loadWords() {
        try {
            final BufferedReader br = new BufferedReader(new InputStreamReader(Kore.class.getClassLoader().getResourceAsStream("assets/GTBWords.txt")));
            String line;
            while ((line = br.readLine()) != null) {
                this.wordList.add(line);
            }
            br.close();
        }
        catch (Exception e) {
            e.printStackTrace();
            Kore.sendMessageWithPrefix("Couldn't load word list!");
        }
    }

    public ArrayList<String> getMatchingWords() {
        final ArrayList<String> list = new ArrayList<String>();
        for (final String word : this.wordList) {
            if (word.length() == tip.length()) {
                boolean matching = true;
                for (int i = 0; i < word.length(); ++i) {
                    if (tip.charAt(i) == '_') {
                        if (word.charAt(i) != ' ') {
                            continue;
                        }
                        matching = false;
                    }
                    if (tip.charAt(i) != word.charAt(i)) {
                        matching = false;
                    }
                    if (!matching) {
                        break;
                    }
                }
                if (!matching) {
                    continue;
                }
                list.add(word);
            }
        }
        return list;
    }
}
