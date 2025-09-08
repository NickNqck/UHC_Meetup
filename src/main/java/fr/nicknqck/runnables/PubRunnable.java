package fr.nicknqck.runnables;

import fr.nicknqck.Main;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class PubRunnable extends BukkitRunnable {

    private int time = 0;
    private int random = 0;
    private int rank = 0;

    @Override
    public void run() {
        if (time == random) {
            if (rank == 0) {
                Bukkit.broadcastMessage("\n§bLa commande§6 /color <joueur1> <joueur2> ect§b est maintenant disponible§b.");
                changeRank(0);
            }
            if (rank == 1) {
                Bukkit.broadcastMessage("\n§bSi vous rencontrez des bugs, n'hésitez surtout pas à le notifier sur le serveur§6 /discord§b.");
                changeRank(1);
            }
            if (rank == 2) {
                Bukkit.broadcastMessage("\n§bIl est maintenant possible de savoir ou vous visez avec votre ROLE ! Via la commande§6 /settings§b.");
                changeRank(2);
            }
            if (rank >= 3){
                Bukkit.broadcastMessage("\n§bUn SoundPack est disponible avec la commande§6 /pack§b.");
                changeRank(3);
            }
            random = Main.RANDOM.nextInt(60*10);
            return;
        }
        this.time++;
    }

    public void start() {
        rank = Main.RANDOM.nextInt(3);
        runTaskTimerAsynchronously(Main.getInstance(), 100, 20);
    }

    private void changeRank(int except) {
        int rdm = Main.RANDOM.nextInt(3);
        while (rdm == except) {
            rdm = Main.RANDOM.nextInt(3);
        }
        rank = rdm;
    }

}