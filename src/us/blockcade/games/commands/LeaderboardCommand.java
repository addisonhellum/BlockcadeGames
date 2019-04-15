package us.blockcade.games.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import us.blockcade.core.util.gui.book.BookUtil;
import us.blockcade.core.util.gui.book.PageBuilder;
import us.blockcade.core.util.gui.book.TextBuilder;
import us.blockcade.core.util.userdata.BPlayer;
import us.blockcade.core.util.userdata.BlockcadeUsers;
import us.blockcade.games.Main;

import java.util.UUID;

public class LeaderboardCommand implements CommandExecutor {

    private String gameName = Main.getGame().getName();

    private ItemStack createBook() {
        PageBuilder page = new PageBuilder()
                .add(
                        TextBuilder.of("Leaderboard:")
                                .color(ChatColor.DARK_BLUE).build()
                )
                .newLine().add(
                        TextBuilder.of(gameName.toUpperCase())
                                .color(ChatColor.BLUE).style(ChatColor.BOLD).build()
                ).newLine().newLine()
                .add(
                        TextBuilder.of(" [Back] ").color(ChatColor.GOLD)
                        .onHover(TextBuilder.HoverAction.showText("Click to return to menu."))
                        .onClick(TextBuilder.ClickAction.runCommand("/menu")).build()
                )
                .add(
                        TextBuilder.of(" [Show More] ").color(ChatColor.DARK_GREEN)
                        .onHover(TextBuilder.HoverAction.showText("Click to view full Leaderboards."))
                        .onClick(TextBuilder.ClickAction.openUrl("https://blockcade.us/games/" +
                                gameName.toLowerCase().replace(" ", "_") + "/leaderboard.php"))
                        .build()
                ).newLine().newLine();

        // TEMPORARY
        String[] leaderboard = new String[] {"Araos", "DuhNiinja", "Jotos", "Sano", "JavaEclipse", "Tesseral", "Notch"};

        for (int i = 1 ; i <= 8 ; i++) {
            String name = "Araos";
            if (i <= leaderboard.length) name = leaderboard[i - 1];

            UUID uuid = Bukkit.getOfflinePlayer(name).getUniqueId();
            BPlayer bplayer = BlockcadeUsers.getBPlayer(uuid);

            ChatColor color = ChatColor.YELLOW;
            if (bplayer.getRank() != null)
                color = bplayer.getRank().getChatColor();

            page.add(
                    TextBuilder.of(i + ". ").color(ChatColor.BLACK).build()
            ).add(
                    TextBuilder.of(name).color(color).build()
            ).newLine();
        }

        return BookUtil.writtenBook()
                .title("How to Play: " + gameName)
                .author("Blockcade")
                .pages(page.build()).build();
    }

    @Override
    public boolean onCommand(CommandSender s, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("leaderboard")) {
            if (!(s instanceof Player)) {
                // TODO: Display leaderboard in Console
                return false;
            }
            Player player = (Player) s;

            BookUtil.openBook(player, createBook());
        }

        return false;
    }

}
