package fr.nicknqck.managers;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.roles.aot.mahr.*;
import fr.nicknqck.roles.aot.soldats.*;
import fr.nicknqck.roles.aot.solo.Eren;
import fr.nicknqck.roles.aot.solo.Gabi;
import fr.nicknqck.roles.aot.solo.TitanUltime;
import fr.nicknqck.roles.aot.titanrouge.*;
import fr.nicknqck.roles.builder.IRole;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.custom.LeComteV2;
import fr.nicknqck.roles.custom.LeJuge;
import fr.nicknqck.roles.krystal.Heldige;
import fr.nicknqck.roles.ds.demons.*;
import fr.nicknqck.roles.ds.demons.lune.*;
import fr.nicknqck.roles.ds.slayers.*;
import fr.nicknqck.roles.ds.slayers.pillier.*;
import fr.nicknqck.roles.ds.solos.*;
import fr.nicknqck.roles.ds.solos.jigorov2.JigoroV2;
import fr.nicknqck.roles.ns.akatsuki.*;
import fr.nicknqck.roles.ns.orochimaru.*;
import fr.nicknqck.roles.ns.orochimaru.edotensei.Kabuto;
import fr.nicknqck.roles.ns.orochimaru.edotensei.Orochimaru;
import fr.nicknqck.roles.ns.shinobi.*;
import fr.nicknqck.roles.ns.shinobi.porte.GaiV2;
import fr.nicknqck.roles.ns.shinobi.porte.RockLeeV2;
import fr.nicknqck.roles.ns.solo.Danzo;
import fr.nicknqck.roles.ns.solo.Gaara;
import fr.nicknqck.roles.ns.solo.jubi.Madara;
import fr.nicknqck.roles.ns.solo.jubi.Obito;
import fr.nicknqck.roles.ns.solo.kumogakure.Ginkaku;
import fr.nicknqck.roles.ns.solo.kumogakure.Kinkaku;
import fr.nicknqck.roles.ns.solo.zabuza_haku.Haku;
import fr.nicknqck.roles.ns.solo.zabuza_haku.Zabuza;
import lombok.Getter;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

@Getter
public class RoleManager {

    private final Map<Class<? extends RoleBase>, IRole> rolesRegistery;
    private final Map<Class<? extends RoleBase>, Integer> rolesEnable;
    public RoleManager() {
        this.rolesRegistery = new HashMap<>();
        this.rolesEnable = new HashMap<>();
        try {
            registerRoles();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void addRole(Class<? extends RoleBase> role) {
        if (rolesEnable.containsKey(role)) {
            int roleCount = rolesEnable.get(role);
            rolesEnable.remove(role, roleCount);
            rolesEnable.put(role, roleCount+1);
        } else {
            rolesEnable.put(role, 1);
        }
    }
    public void removeRole(Class<? extends RoleBase> role) {
        if (rolesEnable.containsKey(role)) {
            int roleCount = rolesEnable.get(role);
            if (rolesEnable.get(role) > 1) {
                rolesEnable.remove(role, roleCount);
                rolesEnable.put(role, roleCount-1);
            } else {
                rolesEnable.remove(role, roleCount);
            }
        } else {
            System.err.println("Error: "+role.getName()+" is not set has enable role");
        }
    }
    private void registerRoles() throws Exception {
        registerDemonSlayer();
        registerAot();
        registerNs();
        registerCustomRoles();
    }
    private void registerRole(Class<? extends RoleBase> roleClass) throws Exception {
        final IRole role = roleClass.getConstructor(UUID.class).newInstance(UUID.randomUUID());
        this.rolesRegistery.put(roleClass, role);
    }
    private void registerDemonSlayer() throws Exception {
        //Register Slayers
        registerRole(GyomeiV2.class);
        registerRole(KanaeV2.class);
        registerRole(KyojuroV2.class);
        registerRole(MitsuriV2.class);
        registerRole(MuichiroV2.class);
        registerRole(ObanaiV2.class);
        registerRole(SanemiV2.class);
        registerRole(ShinobuV2.class);
        registerRole(TomiokaV2.class);
        registerRole(TengenV2.class);
        registerRole(KagayaV2.class);
        registerRole(InosukeV2.class);
        registerRole(HotaruV2.class);
        registerRole(KanaoV2.class);
        registerRole(Makomo.class);
        registerRole(NezukoV2.class);
        registerRole(PourfendeurV2.class);
        registerRole(Sabito.class);
        registerRole(Tanjiro.class);
        registerRole(UrokodakiV2.class);
        registerRole(ZenItsuV2.class);
        //Register Demons
        registerRole(Akaza.class);
        registerRole(Daki.class);
        registerRole(Doma.class);
        registerRole(EnmuV2.class);
        registerRole(Gyokko.class);
        registerRole(GyutaroV2.class);
        registerRole(Hantengu.class);
        registerRole(HantenguV2.class);
        registerRole(KaigakuV2.class);
        registerRole(Kokushibo.class);
        registerRole(Nakime.class);
        registerRole(RuiV2.class);
        registerRole(Demon_Simple.class);
        registerRole(Demon_SimpleV2.class);
        registerRole(DemonMain.class);
        registerRole(Furuto.class);
        registerRole(Kumo.class);
        registerRole(MuzanV2.class);
        registerRole(Susamaru.class);
        registerRole(Yahaba.class);
        //Register Solo
        registerRole(Jigoro.class);
        registerRole(JigoroV2.class);
        registerRole(KyogaiDemon.class);
        registerRole(KyogaiV2.class);
        registerRole(Shinjuro.class);
        registerRole(ShinjuroV2.class);
        registerRole(YoriichiV2.class);
        registerRole(SlayerSolo.class);
    }
    private void registerAot() throws Exception{
        //Register Soldats
        registerRole(Armin.class);
        registerRole(Conny.class);
        registerRole(Eclaireur.class);
        registerRole(Erwin.class);
        registerRole(Hansi.class);
        registerRole(Jean.class);
        registerRole(Livai.class);
        registerRole(Mikasa.class);
        registerRole(Onyankopon.class);
        registerRole(Sasha.class);
        registerRole(Soldat.class);
        //Register Titan Rouge
        registerRole(GrandTitan.class);
        registerRole(Jelena.class);
        registerRole(PetitTitan.class);
        registerRole(TitanBestial.class);
        registerRole(TitanDeviant.class);
        registerRole(TitanSouriant.class);
        //Register Mahr
        registerRole(Bertolt.class);
        registerRole(Lara.class);
        registerRole(Magath.class);
        registerRole(Pieck.class);
        registerRole(Porco.class);
        registerRole(Reiner.class);
        //Register Solo
        registerRole(Eren.class);
        registerRole(Gabi.class);
        registerRole(TitanUltime.class);
    }
    private void registerNs() throws Exception {
        //Register Shinobi
        registerRole(Asuma.class);
        registerRole(Fugaku.class);
        registerRole(Gai.class);
        registerRole(Ino.class);
        registerRole(Jiraya.class);
        registerRole(Kakashi.class);
        registerRole(KillerBee.class);
        registerRole(Konohamaru.class);
        registerRole(Kurenai.class);
        registerRole(Minato.class);
        registerRole(Naruto.class);
        registerRole(RockLee.class);
        registerRole(Sakura.class);
        registerRole(Shikamaru.class);
        registerRole(Tenten.class);
        registerRole(Tsunade.class);
        registerRole(YondaimeRaikage.class);
        registerRole(RockLeeV2.class);
        registerRole(GaiV2.class);
        //Register Orochimaru
        registerRole(Jugo.class);
        registerRole(Kabuto.class);
        registerRole(Karin.class);
        registerRole(Kimimaro.class);
        registerRole(Orochimaru.class);
        registerRole(Sasuke.class);
        registerRole(Suigetsu.class);
        //Register Akatsuki
        registerRole(Deidara.class);
        registerRole(Hidan.class);
        registerRole(Itachi.class);
        registerRole(Kakuzu.class);
        registerRole(Kisame.class);
        registerRole(Konan.class);
        registerRole(Nagato.class);
        registerRole(ZetsuBlanc.class);
        registerRole(ZetsuNoir.class);
        //Register Jubi
        registerRole(Madara.class);
        registerRole(Obito.class);
        //Register Kumogakure
        registerRole(Ginkaku.class);
        registerRole(Kinkaku.class);
        //Register Zabuza et Haku
        registerRole(Zabuza.class);
        registerRole(Haku.class);
        //Register Solo
        registerRole(Danzo.class);
        registerRole(Gaara.class);
    }
    private void registerCustomRoles() throws Exception {
        //Register Custom Roles
        registerRole(LeComteV2.class);
        registerRole(LeJuge.class);
        registerRole(Heldige.class);
    }
    public RoleBase getRandomRole(final UUID uuid) {
        //Si le mec est déjà un GamePlayer, je renvoie null
        if (GameState.getInstance().getGamePlayer().containsKey(uuid))return null;
        //Création d'une Map à partir des rôles activés
        final Map<Class<? extends RoleBase>, Integer> map = new LinkedHashMap<>(getRolesEnable());
        final List<Class<? extends RoleBase>> roleList = new LinkedList<>();
        //J'utilise ce code pour remplir roleList des rôles ayant un nombre supérieur a 0
        for (final Class<? extends RoleBase> classRole : map.keySet()) {
            if (map.getOrDefault(classRole, 0) < 1)continue;
            roleList.add(classRole);
        }
        RoleBase role = null;
        //La je mélance le roleList
        Collections.shuffle(roleList, Main.RANDOM);
        if (!roleList.isEmpty()) {
            //Grace au mélance je get un rôle au hasard
            Class<? extends RoleBase> classRole = roleList.get(0);
            try {
                //La j'instancie le rôle avec l'UUID qui a été donner au début
                role = classRole.getConstructor(UUID.class).newInstance(uuid);
                //La j'envoie des infos à la console
                GameState.getInstance().print(uuid, role);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                     NoSuchMethodException e) {
                //Si ça bug je l'envoie dans la console
                throw new RuntimeException(e);
            }
        }
        return role;
    }
}