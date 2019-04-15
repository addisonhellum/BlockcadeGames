package us.blockcade.games.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import us.blockcade.core.util.format.ChatUtil;
import us.blockcade.core.util.gui.book.BookUtil;
import us.blockcade.core.util.gui.book.PageBuilder;
import us.blockcade.core.util.gui.book.TextBuilder;
import us.blockcade.games.Main;

import java.util.Locale;

public class AboutCommand implements CommandExecutor {

    private String gameName = Main.getGame().getName();

    private ItemStack book = BookUtil.writtenBook()
            .title("How to Play: " + gameName)
            .author("Blockcade")
            .pages(
                    new PageBuilder()
                            .add(
                                    TextBuilder.of("How to Play:")
                                            .color(ChatColor.DARK_BLUE).build()
                            )
                            .newLine().add(
                                    TextBuilder.of(gameName.toUpperCase())
                                            .color(ChatColor.BLUE).style(ChatColor.BOLD).build()
                            ).newLine().newLine()
                            .add(
                                    TextBuilder.of(" [Back] ").color(ChatColor.GOLD)
                                            .onHover(TextBuilder.HoverAction.showText("Click to return to menu."))
                                            .onClick(TextBuilder.ClickAction.runCommand("/menu"))
                                            .build()
                            )
                            .add(
                                    TextBuilder.of(" [More Info]").color(ChatColor.DARK_GREEN)
                                            .onHover(TextBuilder.HoverAction.showText("Click to visit site."))
                                            .onClick(TextBuilder.ClickAction.openUrl("https://blockcade.us/games/" +
                                                gameName.toLowerCase().replace(" ", "_") + "/about.php"))
                                            .build()
                            ).newLine().newLine()
                            .add(
                                    TextBuilder.of(Main.getGame().getInstructions())
                                            .color(ChatColor.BLACK).build()
                            )
                            .build()
            ).build();

    @Override
    public boolean onCommand(CommandSender s, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("about")) {
            if (s instanceof Player) {
                Player player = (Player) s;

                BookUtil.openBook(player, book);

            } else {
                s.sendMessage(ChatUtil.wordWrap(Main.getGame().getInstructions(), 100, Locale.US));
            }
        }

        return false;
    }

}
