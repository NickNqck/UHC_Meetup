package fr.nicknqck.runnables;

import fr.nicknqck.Main;
import fr.nicknqck.roles.desc.AllDesc;
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
                Bukkit.broadcastMessage(AllDesc.bar+"\n\n§bLa commande§6 /color <joueur1> <joueur2> ect§b est maintenant disponible§b.");
            }
            if (rank == 1) {
                Bukkit.broadcastMessage(AllDesc.bar+"\n\n§bSi vous rencontrez des bugs, n'hésitez surtout pas à le notifier sur le serveur§6 /discord§b.");
            }
            if (rank == 2) {
                Bukkit.broadcastMessage(AllDesc.bar+"\n\n§bIl est maintenant possible de savoir ou vous visez avec votre ROLE ! Via la commande§6 /settings§b.");
            }
            if (rank == 3){
                Bukkit.broadcastMessage(AllDesc.bar+"\n\n§bUn SoundPack est disponible avec la commande§6 /pack§b.");
            }
            rank++;
            random = Main.RANDOM.nextInt(60*10);
            return;
        }
        this.time++;
    }
}