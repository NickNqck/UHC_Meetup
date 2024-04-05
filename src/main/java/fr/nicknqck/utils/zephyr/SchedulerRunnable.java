package fr.nicknqck.utils.zephyr;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import fr.nicknqck.Main;
import fr.nicknqck.utils.SchedulerRunnable.IScheduler;

public class SchedulerRunnable {
    private static boolean registered = false;
    public static void CreateDelayedScheduler(Plugin pl, int after, IScheduler iScheduler) {
        new DelayedScheduler(pl, after, iScheduler);
    }


    public static void CreateTimerScheduler(Plugin pl, int amount, int repeat, IScheduler iScheduler) {
        new RepeatScheduler(pl, amount, repeat, iScheduler);
    }

    public static void CreateTimerScheduler(Plugin pl, int repeat, IScheduler iScheduler) {
        new RepeatScheduler(pl, 0, repeat, iScheduler);
    }

    public static void register(Plugin plugin) {
        if(registered) return;
        registered = true;
        new SchedulerRunnableT().runTaskTimer(plugin, 0, 0);
    }

    public static void cancel(IScheduler iScheduler) {
        for (RepeatScheduler repeatScheduler : RepeatScheduler.getRepeatSchedulers()) {
            if (repeatScheduler.getiRepeatScheduler().equals(iScheduler)) {
                repeatScheduler.cancel();
            }
        }
        for (RepeatScheduler repeatScheduler : RepeatScheduler.getRegisterSchedulers()) {
            if (repeatScheduler.getiRepeatScheduler().equals(iScheduler)) {
                repeatScheduler.cancel();
            }
        }
        for (DelayedScheduler delayedScheduler : DelayedScheduler.getRepeatSchedulers()) {
            if (delayedScheduler.getiRepeatScheduler().equals(iScheduler)) {
                delayedScheduler.cancel();

            }
        }
        for (DelayedScheduler delayedScheduler : DelayedScheduler.getRegisterSchedulers()) {
            if (delayedScheduler.getiRepeatScheduler().equals(iScheduler)) {
                delayedScheduler.cancel();
            }
        }
    }

    public static void cancel(Plugin plugin) {
        if (plugin.equals(Main.getInstance())) return;
        for (RepeatScheduler repeatScheduler : RepeatScheduler.getRepeatSchedulers()) {
            if (repeatScheduler.getPlugin().equals(plugin)) {
                repeatScheduler.cancel();
            }
        }
        for (RepeatScheduler repeatScheduler : RepeatScheduler.getRegisterSchedulers()) {
            if (repeatScheduler.getPlugin().equals(plugin)) {
                repeatScheduler.cancel();
            }
        }
        for (DelayedScheduler delayedScheduler : DelayedScheduler.getRepeatSchedulers()) {
            if (delayedScheduler.getPlugin().equals(plugin)) {
                delayedScheduler.cancel();
            }
        }
        for (DelayedScheduler delayedScheduler : DelayedScheduler.getRegisterSchedulers()) {
            if (delayedScheduler.getPlugin().equals(plugin)) {
                delayedScheduler.cancel();
            }
        }
    }
}

class SchedulerRunnableT extends BukkitRunnable {
    @Override
    public void run() {
        List<RepeatScheduler> toRemovea = new ArrayList<>();
        List<RepeatScheduler> add2 = new ArrayList<>(RepeatScheduler.getRepeatSchedulers());
        add2.forEach(RepeatScheduler::register);
        add2.clear();
        RepeatScheduler.getRepeatSchedulers().clear();
        for (RepeatScheduler repeatScheduler : RepeatScheduler.getRegisterSchedulers()) {
            if (repeatScheduler.isCancelled()) {
                toRemovea.add(repeatScheduler);
            }
            if (!repeatScheduler.isCancelled()) {
                if (repeatScheduler.getRepeat() == repeatScheduler.getTime()) {
                    repeatScheduler.accept();
                    repeatScheduler.setTime(0);
                    if (repeatScheduler.getAmount() != 0) {
                        repeatScheduler.addTimet(1);
                        if (repeatScheduler.getTimet() == repeatScheduler.getAmount()) {
                            toRemovea.add(repeatScheduler);
                        }
                    }
                } else {
                    repeatScheduler.addTime(1);
                }
            }
        }
        toRemovea.forEach(RepeatScheduler::unregister);

        List<DelayedScheduler> toRemove = new ArrayList<>();
        List<DelayedScheduler> add = new ArrayList<>(DelayedScheduler.getRepeatSchedulers());
        add.forEach(DelayedScheduler::register);
        add.clear();
        DelayedScheduler.getRepeatSchedulers().clear();
        for (DelayedScheduler delayedScheduler : DelayedScheduler.getRegisterSchedulers()) {
            if (delayedScheduler.isCancelled()) {
                toRemove.add(delayedScheduler);
            }
            if (!delayedScheduler.isCancelled()) {
                if (delayedScheduler.getRepeat() == delayedScheduler.getTime()) {
                    toRemove.add(delayedScheduler);
                    delayedScheduler.accept();
                } else {
                    delayedScheduler.addTime(1);
                }
            }
        }
        toRemove.forEach(DelayedScheduler::unregister);
    }
}

class DelayedScheduler {
    private int time = 0;
    private int repeat;
    private IScheduler iRepeatScheduler;
    private static List<DelayedScheduler> repeatSchedulers = new ArrayList<>();
    private static List<DelayedScheduler> registerSchedulers = new ArrayList<>();
    private boolean cancelled;
    private Plugin plugin;

    public Plugin getPlugin() {
        return plugin;
    }

    public DelayedScheduler(Plugin plugin, int after, IScheduler iRepeatScheduler) {
        this.plugin = plugin;
        this.repeat = after;
        this.iRepeatScheduler = iRepeatScheduler;
        addRepeatSchedulers(this);
    }

    public static List<DelayedScheduler> getRegisterSchedulers() {
        return registerSchedulers;
    }

    void register() {
        repeatSchedulers.remove(this);
        registerSchedulers.add(this);
    }

    void unregister() {
        registerSchedulers.remove(this);
        repeatSchedulers.remove(this);
    }

    void accept() {
        iRepeatScheduler.run();
    }

    int getRepeat() {
        return repeat;
    }

    void addTime(int i) {
        this.time = i + getTime();
    }

    void setTime(int i) {
        this.time = i;
    }

    int getTime() {
        return time;
    }

    void cancel() {
        this.cancelled = true;
    }

    boolean isCancelled() {
        return cancelled;
    }

    public IScheduler getiRepeatScheduler() {
        return iRepeatScheduler;
    }

    public static List<DelayedScheduler> getRepeatSchedulers() {
        return repeatSchedulers;
    }

    public void addRepeatSchedulers(DelayedScheduler repeatSchedulers) {
        DelayedScheduler.repeatSchedulers.add(repeatSchedulers);
    }
}

class RepeatScheduler {
    private int amount = 0;
    private int time = 0;
    private int timet = 0;
    private int repeat = 0;
    private static List<RepeatScheduler> repeatSchedulers = new ArrayList<>();
    private IScheduler iRepeatScheduler;
    private static List<RepeatScheduler> registerSchedulers = new ArrayList<>();
    private boolean cancelled;
    private Plugin plugin;

    public Plugin getPlugin() {
        return plugin;
    }

    public RepeatScheduler(Plugin plugin, int amount, int repeat, IScheduler iRepeatScheduler) {
        this.plugin = plugin;
        this.amount = amount;
        this.repeat = repeat;
        this.iRepeatScheduler = iRepeatScheduler;
        addRepeatSchedulers(this);
    }

    int getAmount() {
        return amount;
    }

    void unregister() {
        registerSchedulers.remove(this);
        repeatSchedulers.remove(this);
    }

    public static List<RepeatScheduler> getRegisterSchedulers() {
        return registerSchedulers;
    }

    void register() {
        repeatSchedulers.remove(this);
        registerSchedulers.add(this);
    }

    void accept() {
        iRepeatScheduler.run();
    }

    void cancel() {
        this.cancelled = true;
    }

    boolean isCancelled() {
        return cancelled;
    }

    void setTime(int i) {
        this.time = i;
    }

    public IScheduler getiRepeatScheduler() {
        return iRepeatScheduler;
    }

    int getRepeat() {
        return repeat;
    }

    void addTime(int i) {
        this.time = i + getTime();
    }

    int getTime() {
        return time;
    }

    void addTimet(int i) {
        this.timet = i + getTimet();
    }

    int getTimet() {
        return timet;
    }

    public static List<RepeatScheduler> getRepeatSchedulers() {
        return repeatSchedulers;
    }

    public void addRepeatSchedulers(RepeatScheduler repeatSchedulers) {
        RepeatScheduler.repeatSchedulers.add(repeatSchedulers);
    }
}

