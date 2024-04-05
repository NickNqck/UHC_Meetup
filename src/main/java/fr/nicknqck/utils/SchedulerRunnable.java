package fr.nicknqck.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import org.apache.commons.lang3.ObjectUtils;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import fr.nicknqck.Main;

public class SchedulerRunnable {
    private static boolean registered = false;
    public interface IScheduler {
        void run();
    }
    public static void createDelayedScheduler( Plugin pl, int after, IScheduler iScheduler) {
        new DelayedScheduler(pl, after, iScheduler,null);
    }
    public static void createDelayedScheduler(Plugin pl, int after, IScheduler iScheduler,@Nullable Predicate<ObjectUtils.Null> predicate) {
        new DelayedScheduler(pl, after, iScheduler,predicate);
    }
    public static void createTimerScheduler(Plugin pl, int amount, int repeat, IScheduler iScheduler,@Nullable Predicate<ObjectUtils.Null> predicate,@Nullable Predicate<ObjectUtils.Null> stoppredicate) {
        new RepeatScheduler(pl, amount, repeat, iScheduler, predicate,stoppredicate);
    }
    public static void createTimerScheduler( Plugin pl, int amount, int repeat, IScheduler iScheduler,@Nullable Predicate<ObjectUtils.Null> predicate) {
        new RepeatScheduler(pl, amount, repeat, iScheduler, predicate,null);
    }
    public static void createTimerScheduler( Plugin pl, int amount, int repeat, IScheduler iScheduler) {
        new RepeatScheduler(pl, amount, repeat, iScheduler,null,null);
    }
    public static void createTimerScheduler( Plugin pl, int repeat, IScheduler iScheduler) {
        new RepeatScheduler(pl, 0, repeat, iScheduler,null,null);
    }
    public static void createTimerScheduler( Plugin pl, int repeat, IScheduler iScheduler,@Nullable Predicate<ObjectUtils.Null> predicate,@Nullable Predicate<ObjectUtils.Null> stoppredicate) {
        new RepeatScheduler(pl, 0,repeat, iScheduler, predicate,stoppredicate);
    }
    public static void createTimerScheduler( Plugin pl, int repeat, IScheduler iScheduler,@Nullable Predicate<ObjectUtils.Null> predicate) {
        new RepeatScheduler(pl, 0, repeat, iScheduler, predicate,null);
    }

    public static void register( Plugin plugin) {
        if (registered) return;
        registered = true;
        new SchedulerRunnableT().runTaskTimer(plugin, 0, 0);
    }

    public static void cancel( IScheduler iScheduler) {
        RepeatScheduler.getAllSchedulers()
                .stream()
                .filter(repeatScheduler -> repeatScheduler.getiRepeatScheduler().equals(iScheduler))
                .forEach(RepeatScheduler::cancel);
        DelayedScheduler.getAllSchedulers()
                .stream()
                .filter(delayedScheduler -> delayedScheduler.getiScheduler().equals(iScheduler))
                .forEach(DelayedScheduler::cancel);
    }

    public static void cancel( Plugin plugin) {
        if (plugin.equals(Main.getPlugin(null))) return;
        RepeatScheduler.getAllSchedulers()
                .stream()
                .filter(repeatScheduler -> repeatScheduler.getPlugin().equals(plugin))
                .forEach(RepeatScheduler::cancel);
        DelayedScheduler.getAllSchedulers()
                .stream()
                .filter(delayedScheduler -> delayedScheduler.getPlugin().equals(plugin))
                .forEach(DelayedScheduler::cancel);
    }


    private static class DelayedScheduler {
        private int time = 0;
        private final int repeat;
        private final IScheduler iScheduler;
        private static final List<DelayedScheduler> delayedSchedulers = new ArrayList<>();
        private static final List<DelayedScheduler> registerSchedulers = new ArrayList<>();
        private boolean cancelled;
        private final Plugin plugin;
        private Predicate<ObjectUtils.Null> predicate;

        private Plugin getPlugin() {
            return plugin;
        }

        private DelayedScheduler( Plugin plugin, int after, IScheduler iRepeatScheduler,@Nullable Predicate<ObjectUtils.Null> predicate) {
            this.plugin = plugin;
            this.repeat = after;
            this.iScheduler = iRepeatScheduler;
            addRepeatSchedulers(this);
            this.predicate = predicate;
        }

        private static List<DelayedScheduler> getRegisterSchedulers() {
            return registerSchedulers;
        }

        private void register() {
            delayedSchedulers.remove(this);
            registerSchedulers.add(this);
        }

        private void unregister() {
            registerSchedulers.remove(this);
            delayedSchedulers.remove(this);
        }

        private static List<DelayedScheduler> getAllSchedulers() {
            List<DelayedScheduler> schedulers = new ArrayList<>();
            schedulers.addAll(registerSchedulers);
            schedulers.addAll(delayedSchedulers);
            return schedulers;
        }

        private void accept() {
            iScheduler.run();
        }

        private boolean test(){
            if(predicate == null) return false;
            return predicate.test(null);
        }

        private int getRepeat() {
            return repeat;
        }

        private void addTime(int i) {
            this.time = i + getTime();
        }

        @SuppressWarnings("unused")
		private void setTime(int i) {
            this.time = i;
        }

        private int getTime() {
            return time;
        }

        private void cancel() {
            this.cancelled = true;
        }

        private boolean isCancelled() {
            return cancelled;
        }

        private IScheduler getiScheduler() {
            return iScheduler;
        }

        private static List<DelayedScheduler> getDelayedSchedulers() {
            return delayedSchedulers;
        }

        private void addRepeatSchedulers(DelayedScheduler delayedScheduler) {
            getDelayedSchedulers().add(delayedScheduler);
        }
    }

    private static class RepeatScheduler {
        private int amount;
        private int time = 0;
        private int timet = 0;
        private int repeat;
        private static List<RepeatScheduler> repeatSchedulers = new ArrayList<>();
        private IScheduler iRepeatScheduler;
        private static List<RepeatScheduler> registerSchedulers = new ArrayList<>();
        private boolean cancelled;
        private Plugin plugin;
        private Predicate<ObjectUtils.Null> predicate;
        private Predicate<ObjectUtils.Null> stopPredicate;

        private Plugin getPlugin() {
            return plugin;
        }

        private RepeatScheduler( Plugin plugin, int amount, int repeat,  IScheduler iRepeatScheduler,@Nullable Predicate<ObjectUtils.Null> predicate, @Nullable Predicate<ObjectUtils.Null> stopPredicate) {
            this.plugin = plugin;
            this.amount = amount;
            this.repeat = repeat;
            this.iRepeatScheduler = iRepeatScheduler;
            addRepeatSchedulers(this);
            this.predicate = predicate;
            this.stopPredicate = stopPredicate;
        }

        private int getAmount() {
            return amount;
        }

        private boolean test(){
            if(predicate == null) return false;
            return predicate.test(null);
        }
        private boolean teststop(){
            if(stopPredicate == null) return false;
            return stopPredicate.test(null);
        }

        private void unregister() {
            registerSchedulers.remove(this);
            repeatSchedulers.remove(this);
        }

        private static List<RepeatScheduler> getRegisterSchedulers() {
            return registerSchedulers;
        }

        private static List<RepeatScheduler> getAllSchedulers() {
            List<RepeatScheduler> schedulers = new ArrayList<>();
            schedulers.addAll(registerSchedulers);
            schedulers.addAll(repeatSchedulers);
            return schedulers;
        }

        private void register() {
            repeatSchedulers.remove(this);
            registerSchedulers.add(this);
        }

        private void accept() {
            iRepeatScheduler.run();
        }

        private void cancel() {
            this.cancelled = true;
        }

        private boolean isCancelled() {
            return cancelled;
        }

        private void setTime(int i) {
            this.time = i;
        }

        private IScheduler getiRepeatScheduler() {
            return iRepeatScheduler;
        }

        private int getRepeat() {
            return repeat;
        }

        private void addTime(int i) {
            this.time = i + getTime();
        }

        private int getTime() {
            return time;
        }

        private void addTimet(int i) {
            this.timet = i + getTimet();
        }

        private int getTimet() {
            return timet;
        }

        private static List<RepeatScheduler> getRepeatSchedulers() {
            return repeatSchedulers;
        }

        private void addRepeatSchedulers(RepeatScheduler repeatSchedulers) {
            RepeatScheduler.repeatSchedulers.add(repeatSchedulers);
        }
    }

    private static class SchedulerRunnableT extends BukkitRunnable {
        @Override
        public void run() {
            List<RepeatScheduler> toRemovea = new ArrayList<>();
            List<RepeatScheduler> add2 = new ArrayList<>(RepeatScheduler.getRepeatSchedulers());
            add2.forEach(RepeatScheduler::register);
            add2.clear();//to avoid exception


            RepeatScheduler.getRepeatSchedulers().clear();
            for (RepeatScheduler repeatScheduler : RepeatScheduler.getRegisterSchedulers()) {
                if (repeatScheduler.isCancelled()){
                    toRemovea.add(repeatScheduler);
                    continue;
                }


                if (repeatScheduler.getRepeat() == repeatScheduler.getTime()) {

                    if(repeatScheduler.test()){
                        repeatScheduler.setTime(0);
                        continue;
                    }

                    if(repeatScheduler.teststop()){
                        toRemovea.add(repeatScheduler);
                        continue;
                    }

                    repeatScheduler.accept();
                    repeatScheduler.setTime(0);
                    if (repeatScheduler.getAmount() == 0) continue;
                    repeatScheduler.addTimet(1);
                    if (repeatScheduler.getTimet() == repeatScheduler.getAmount()) toRemovea.add(repeatScheduler);
                    continue;
                }

                repeatScheduler.addTime(1);
            }

            toRemovea.forEach(RepeatScheduler::unregister);

            List<DelayedScheduler> toRemove = new ArrayList<>();
            List<DelayedScheduler> add = new ArrayList<>(DelayedScheduler.getDelayedSchedulers());
            add.forEach(DelayedScheduler::register);
            add.clear();//to avoid exception


            DelayedScheduler.getDelayedSchedulers().clear();
            for (DelayedScheduler delayedScheduler : DelayedScheduler.getRegisterSchedulers()) {
                if (delayedScheduler.isCancelled()) toRemove.add(delayedScheduler);

                if (delayedScheduler.isCancelled()) continue;
                if (delayedScheduler.getRepeat() == delayedScheduler.getTime()) {
                    if(delayedScheduler.test()){
                        toRemove.add(delayedScheduler);
                        continue;
                    }
                    toRemove.add(delayedScheduler);
                    delayedScheduler.accept();
                    continue;
                }
                delayedScheduler.addTime(1);

            }
            toRemove.forEach(DelayedScheduler::unregister);
        }
    }
}