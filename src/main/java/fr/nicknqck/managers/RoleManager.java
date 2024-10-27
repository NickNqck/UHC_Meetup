package fr.nicknqck.managers;

import fr.nicknqck.roles.aot.mahr.*;
import fr.nicknqck.roles.aot.soldats.*;
import fr.nicknqck.roles.aot.solo.Eren;
import fr.nicknqck.roles.aot.solo.Gabi;
import fr.nicknqck.roles.aot.solo.TitanUltime;
import fr.nicknqck.roles.aot.titanrouge.*;
import fr.nicknqck.roles.builder.IRole;
import fr.nicknqck.roles.custom.LeComte;
import fr.nicknqck.roles.custom.LeJuge;
import fr.nicknqck.roles.ds.demons.*;
import fr.nicknqck.roles.ds.demons.lune.*;
import fr.nicknqck.roles.ds.slayers.*;
import fr.nicknqck.roles.ds.slayers.pillier.*;
import fr.nicknqck.roles.ds.solos.*;
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

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public class RoleManager {

    private final Map<Class<? extends IRole>, IRole> rolesRegistery;
    private final Map<Class<? extends IRole>, Integer> rolesEnable;
    public RoleManager() {
        this.rolesRegistery = new HashMap<>();
        this.rolesEnable = new HashMap<>();
        try {
            registerRoles();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void addRole(Class<? extends IRole> role) {
        if (rolesEnable.containsKey(role)) {
            int roleCount = rolesEnable.get(role);
            rolesEnable.remove(role, roleCount);
            rolesEnable.put(role, roleCount+1);
        } else {
            rolesEnable.put(role, 1);
        }
    }
    public void removeRole(Class<? extends IRole> role) {
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
    private void registerRole(Class<? extends IRole> roleClass) throws Exception {
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
        registerRole(Kagaya.class);
        registerRole(Inosuke.class);
        registerRole(HotaruV2.class);
        registerRole(Kanao.class);
        registerRole(Makomo.class);
        registerRole(Nezuko.class);
        registerRole(PourfendeurV2.class);
        registerRole(Sabito.class);
        registerRole(Tanjiro.class);
        registerRole(Urokodaki.class);
        registerRole(ZenItsu.class);
        //Register Demons
        registerRole(Akaza.class);
        registerRole(Daki.class);
        registerRole(Doma.class);
        registerRole(Enmu.class);
        registerRole(Gyokko.class);
        registerRole(Gyutaro.class);
        registerRole(Hantengu.class);
        registerRole(HantenguV2.class);
        registerRole(Kaigaku.class);
        registerRole(Kokushibo.class);
        registerRole(Nakime.class);
        registerRole(Rui.class);
        registerRole(Demon_Simple.class);
        registerRole(Demon_SimpleV2.class);
        registerRole(DemonMain.class);
        registerRole(Furuto.class);
        registerRole(Kumo.class);
        registerRole(Muzan.class);
        registerRole(Susamaru.class);
        registerRole(Yahaba.class);
        //Register Solo
        registerRole(Jigoro.class);
        registerRole(JigoroV2.class);
        registerRole(Kyogai.class);
        registerRole(KyogaiV2.class);
        registerRole(Shinjuro.class);
        registerRole(ShinjuroV2.class);
        registerRole(Yoriichi.class);
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
        registerRole(LeComte.class);
        registerRole(LeJuge.class);
    }
}