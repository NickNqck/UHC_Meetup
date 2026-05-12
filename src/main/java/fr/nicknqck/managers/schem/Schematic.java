package fr.nicknqck.managers.schem;

import fr.nicknqck.Main;
import fr.nicknqck.utils.Cuboid;
import lombok.Getter;
import net.minecraft.server.v1_8_R3.Block;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.IBlockData;
import net.minecraft.server.v1_8_R3.NBTCompressedStreamTools;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.NBTTagList;
import net.minecraft.server.v1_8_R3.TileEntity;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Représente un schematic MCEdit (.schematic) chargé en mémoire.
 *
 * <p>Format MCEdit (.schematic) — structure NBT attendue :
 * <pre>
 *   TAG_Compound (root)
 *     "Width"         TAG_Short  → dimension X
 *     "Height"        TAG_Short  → dimension Y
 *     "Length"        TAG_Short  → dimension Z
 *     "Blocks"        TAG_Byte_Array → IDs des blocs (8 bits bas)
 *     "Data"          TAG_Byte_Array → métadonnées des blocs
 *     "AddBlocks"     TAG_Byte_Array → bits hauts des IDs > 255 (optionnel, rare en 1.8)
 *     "TileEntities"  TAG_List[TAG_Compound] → données des TileEntities
 *     "WEOffsetX/Y/Z" TAG_Int → offset WorldEdit (optionnel)
 * </pre>
 *
 * <p>Index d'un bloc : {@code (y * Length + z) * Width + x}
 *
 * <p>NMS utilisé : net.minecraft.server.v1_8_R3 (Spigot 1.8.8 / v1_8_R3)
 */
public class Schematic {

    // ─── Dimensions ───────────────────────────────────────────────────────────

    /** Dimension sur l'axe X.
     * -- GETTER --
     *
     * @return Dimension sur l'axe X (Ouest → Est).
     */
    @Getter
    private final int width;
    /** Dimension sur l'axe Y.
     * -- GETTER --
     *
     * @return Dimension sur l'axe Y (bas → haut).
     */
    @Getter
    private final int height;
    /** Dimension sur l'axe Z.
     * -- GETTER --
     *
     * @return Dimension sur l'axe Z (Nord → Sud).
     */
    @Getter
    private final int length;

    // ─── Données brutes des blocs ──────────────────────────────────────────────

    /**
     * IDs de blocs, indexés par {@code (y * length + z) * width + x}.
     * Ne contient que les 8 bits bas ; {@link #addBlocks} fournit les bits hauts (IDs > 255).
     */
    private final byte[] blocks;

    /**
     * Métadonnées (data value) de chaque bloc, même indexation que {@link #blocks}.
     * Seuls les 4 bits bas sont significatifs (valeurs 0-15).
     */
    private final byte[] blockData;

    /**
     * Extension pour les IDs de blocs > 255 (champ "AddBlocks", optionnel).
     * Chaque octet encode 4 bits hauts pour deux blocs consécutifs.
     * Tableau vide si le champ est absent du fichier.
     */
    private final byte[] addBlocks;

    // ─── TileEntities ─────────────────────────────────────────────────────────

    /**
     * Données NBT des TileEntities (coffres, panneaux, etc.).
     * Les coordonnées sont <em>relatives</em> à l'origine du schematic.
     */
    private final List<NBTTagCompound> tileEntityData;

    // ─── Offset WorldEdit ─────────────────────────────────────────────────────

    /**
     * Offset WorldEdit, présent si le schematic a été exporté via WorldEdit.
     * Permet de recentrer le collage sur le point de sélection d'origine.
     * -- GETTER --
     *
     * @return Offset X exporté par WorldEdit (0 si absent).

     */
    @Getter
    private final int weOffsetX;
    /**
     * @return Offset Y exporté par WorldEdit (0 si absent).
     */
    @Getter
    private final int weOffsetY;
    /**
     * @return Offset Z exporté par WorldEdit (0 si absent).
     */
    @Getter
    private final int weOffsetZ;

    // ──────────────────────────────────────────────────────────────────────────
    // Constructeur privé — utiliser {@link #load(File)}
    // ──────────────────────────────────────────────────────────────────────────

    private Schematic(int width, int height, int length,
                      byte[] blocks, byte[] blockData, byte[] addBlocks,
                      List<NBTTagCompound> tileEntityData,
                      int weOffsetX, int weOffsetY, int weOffsetZ) {
        this.width        = width;
        this.height       = height;
        this.length       = length;
        this.blocks       = blocks;
        this.blockData    = blockData;
        this.addBlocks    = addBlocks;
        this.tileEntityData = tileEntityData;
        this.weOffsetX    = weOffsetX;
        this.weOffsetY    = weOffsetY;
        this.weOffsetZ    = weOffsetZ;
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Chargement depuis le disque
    // ──────────────────────────────────────────────────────────────────────────

    /**
     * Charge et parse un fichier {@code .schematic} (format MCEdit, GZip+NBT).
     *
     * <p>Utilise {@code NBTCompressedStreamTools.a(InputStream)} de NMS v1_8_R3
     * pour décompresser et lire le flux NBT binaire.
     *
     * @param file Le fichier {@code .schematic} à charger. Ne doit pas être null.
     * @return Un objet {@link Schematic} prêt à l'emploi.
     * @throws IOException              Si la lecture du fichier échoue.
     * @throws IllegalArgumentException Si le contenu NBT est incomplet ou invalide.
     */
    public static Schematic load(File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file)) {

            // NBTCompressedStreamTools.a() décompresse le GZip et parse le NBT.
            // Méthode statique NMS v1_8_R3, équivalente à "readCompressed".
            NBTTagCompound nbt = NBTCompressedStreamTools.a(fis);

            // ── Validation des champs obligatoires ────────────────────────────
            if (!nbt.hasKey("Width") || !nbt.hasKey("Height") || !nbt.hasKey("Length")) {
                throw new IllegalArgumentException(
                        "Format invalide : les champs 'Width', 'Height' ou 'Length' sont absents."
                );
            }
            if (!nbt.hasKey("Blocks") || !nbt.hasKey("Data")) {
                throw new IllegalArgumentException(
                        "Format invalide : les champs 'Blocks' ou 'Data' sont absents."
                );
            }

            // ── Lecture des dimensions ────────────────────────────────────────
            short width = nbt.getShort("Width");
            short height = nbt.getShort("Height");
            short length = nbt.getShort("Length");

            // ── Lecture des blocs ─────────────────────────────────────────────
            byte[] blocks = nbt.getByteArray("Blocks");
            byte[] data = nbt.getByteArray("Data");

            // Tag type 7 = TAG_Byte_Array
            byte[] addBlocks = nbt.hasKeyOfType("AddBlocks", 7)
                    ? nbt.getByteArray("AddBlocks")
                    : new byte[0];

            // ── Lecture des TileEntities ──────────────────────────────────────
            List<NBTTagCompound> tileEntities = new ArrayList<NBTTagCompound>();
            // Tag type 9 = TAG_List, type d'élément 10 = TAG_Compound
            if (nbt.hasKeyOfType("TileEntities", 9)) {
                NBTTagList teList = nbt.getList("TileEntities", 10);
                for (int i = 0; i < teList.size(); i++) {
                    tileEntities.add((NBTTagCompound) teList.get(i));
                }
            }

            // ── Lecture de l'offset WorldEdit (optionnel) ────────────────────
            int weOffX = nbt.hasKey("WEOffsetX") ? nbt.getInt("WEOffsetX") : 0;
            int weOffY = nbt.hasKey("WEOffsetY") ? nbt.getInt("WEOffsetY") : 0;
            int weOffZ = nbt.hasKey("WEOffsetZ") ? nbt.getInt("WEOffsetZ") : 0;

            return new Schematic(
                    width, height, length,
                    blocks, data, addBlocks,
                    tileEntities,
                    weOffX, weOffY, weOffZ
            );

        }
        /* flux déjà fermé */
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Collage synchrone
    // ──────────────────────────────────────────────────────────────────────────

    /**
     * Colle le schematic de manière <strong>synchrone</strong> (immédiate, sur le thread principal).
     *
     * <p><strong>Attention :</strong> Pour les schematics volumineux (> quelques milliers de blocs),
     * préférer {@link #pasteSpread(Location, boolean, Plugin, int, Runnable)} pour éviter
     * de bloquer le thread principal.
     *
     * @param origin    Coin inférieur-gauche-avant (angle bas-nord-ouest) du schematic.
     * @param ignoreAir Si {@code true}, les blocs d'air du schematic ne remplacent pas
     *                  les blocs existants dans le monde.
     */
    public void paste(Location origin, boolean ignoreAir) {
        net.minecraft.server.v1_8_R3.World nmsWorld =
                ((CraftWorld) origin.getWorld()).getHandle();

        int ox = origin.getBlockX();
        int oy = origin.getBlockY();
        int oz = origin.getBlockZ();

        for (int y = 0; y < height; y++) {
            for (int z = 0; z < length; z++) {
                for (int x = 0; x < width; x++) {
                    int index = (y * length + z) * width + x;
                    int blockId = resolveBlockId(index);
                    int dataVal = blockData[index] & 0x0F; // 4 bits bas uniquement

                    if (ignoreAir && blockId == 0) continue;

                    Block nmsBlock = Block.getById(blockId);
                    if (nmsBlock == null) continue;

                    BlockPosition bp  = new BlockPosition(ox + x, oy + y, oz + z);
                    IBlockData    ibd = nmsBlock.fromLegacyData(dataVal);
                    // Flag 2 : envoie la mise à jour aux clients, sans déclencher la physique.
                    nmsWorld.setTypeAndData(bp, ibd, 2);
                }
            }
        }

        pasteTileEntities(nmsWorld, ox, oy, oz);
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Collage étalé sur plusieurs ticks (pour les grands schematics)
    // ──────────────────────────────────────────────────────────────────────────

    /**
     * Colle le schematic de manière progressive, en étalant le travail sur plusieurs ticks.
     *
     * <p>Toutes les opérations de bloc restent sur le <strong>thread principal</strong>
     * (via un {@link BukkitRunnable} synchrone), ce qui est obligatoire pour modifier le monde
     * en toute sécurité. La progressivité évite uniquement le gel du serveur.
     *
     * <p>Exemple d'utilisation :
     * <pre>{@code
     * schematic.pasteSpread(location, true, plugin, 2000, () ->
     *     Bukkit.broadcastMessage("Collage terminé !")
     * );
     * }</pre>
     *
     * @param origin        Coin bas-nord-ouest du schematic.
     * @param ignoreAir     Si {@code true}, l'air ne remplace pas les blocs existants.
     * @param plugin        Instance du plugin (nécessaire pour le scheduler Bukkit).
     * @param blocksPerTick Nombre de blocs posés par tick (recommandé : 1 000 à 5 000).
     *                      Plus la valeur est haute, plus le collage est rapide mais risque
     *                      de causer de légères saccades.
     * @param onComplete    Callback appelé sur le thread principal une fois le collage terminé.
     *                      Peut être {@code null}.
     */
    public void pasteSpread(final Location origin, final boolean ignoreAir,
                            Plugin plugin, final int blocksPerTick,
                            final Runnable onComplete) {

        // Pré-calcul de toutes les opérations : évite les boucles imbriquées à chaque tick.
        // Format : { x, y, z, blockId, dataVal }
        final List<int[]> operations = buildOperationList(ignoreAir);
        if (operations.isEmpty()) {
            if (onComplete != null) onComplete.run();
            return;
        }

        final int ox = origin.getBlockX();
        final int oy = origin.getBlockY();
        final int oz = origin.getBlockZ();
        final int[] cursor = {0};

        new BukkitRunnable() {
            @Override
            public void run() {
                // setTypeAndData DOIT être appelé sur le thread principal — c'est le cas ici.
                net.minecraft.server.v1_8_R3.World nmsWorld =
                        ((CraftWorld) origin.getWorld()).getHandle();

                int end = Math.min(cursor[0] + blocksPerTick, operations.size());

                for (int i = cursor[0]; i < end; i++) {
                    int[] op = operations.get(i);
                    // op[0]=x, op[1]=y, op[2]=z, op[3]=blockId, op[4]=dataVal
                    Block nmsBlock = Block.getById(op[3]);
                    if (nmsBlock == null) continue;

                    BlockPosition bp  = new BlockPosition(ox + op[0], oy + op[1], oz + op[2]);
                    IBlockData    ibd = nmsBlock.fromLegacyData(op[4]);
                    nmsWorld.setTypeAndData(bp, ibd, 2);
                }

                cursor[0] = end;

                // Fin du collage : on place les TileEntities puis on notifie.
                if (cursor[0] >= operations.size()) {
                    cancel();
                    pasteTileEntities(
                            ((CraftWorld) origin.getWorld()).getHandle(),
                            ox, oy, oz
                    );
                    if (onComplete != null) {
                        onComplete.run();
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Méthodes privées utilitaires
    // ──────────────────────────────────────────────────────────────────────────

    /**
     * Résout l'ID complet d'un bloc à un index donné, en tenant compte
     * du champ {@code AddBlocks} pour les IDs > 255.
     *
     * <p>Chaque octet de {@code AddBlocks} encode les 4 bits hauts de deux blocs :
     * <ul>
     *   <li>Bits 0-3 pour le bloc d'index pair ({@code index & 1 == 0})</li>
     *   <li>Bits 4-7 pour le bloc d'index impair</li>
     * </ul>
     *
     * @param index Index linéaire dans le tableau {@link #blocks}.
     * @return L'ID de bloc complet (0 – 4095).
     */
    private int resolveBlockId(int index) {
        int id = blocks[index] & 0xFF;
        if (addBlocks.length > 0) {
            int addByte = addBlocks[index >> 1] & 0xFF;
            if ((index & 1) == 0) {
                id |= (addByte & 0x0F) << 8; // bits 0-3 pour index pair
            } else {
                id |= (addByte >> 4)    << 8; // bits 4-7 pour index impair
            }
        }
        return id;
    }

    /**
     * Construit la liste aplatie des opérations de bloc à effectuer,
     * utilisée par {@link #pasteSpread}.
     *
     * @param ignoreAir Si {@code true}, les blocs d'air sont exclus de la liste.
     * @return Liste de tableaux {@code [x, y, z, blockId, dataVal]}.
     */
    private List<int[]> buildOperationList(boolean ignoreAir) {
        List<int[]> ops = new ArrayList<int[]>(width * height * length);
        for (int y = 0; y < height; y++) {
            for (int z = 0; z < length; z++) {
                for (int x = 0; x < width; x++) {
                    int index   = (y * length + z) * width + x;
                    int blockId = resolveBlockId(index);
                    int dataVal = blockData[index] & 0x0F;
                    if (ignoreAir && blockId == 0) continue;
                    ops.add(new int[]{x, y, z, blockId, dataVal});
                }
            }
        }
        return ops;
    }

    /**
     * Applique les TileEntities du schematic dans le monde cible.
     *
     * <p>Doit impérativement être appelée <em>après</em> la pose des blocs,
     * car la TileEntity est instanciée par le serveur lors du {@code setTypeAndData}.
     * Cette méthode récupère l'instance existante et lui injecte les données NBT.
     *
     * <p>Méthodes NMS utilisées :
     * <ul>
     *   <li>{@code World.getTileEntity(BlockPosition)} — récupère la TE existante.</li>
     *   <li>{@code TileEntity.a(NBTTagCompound)} — charge les données NBT dans la TE
     *       (= {@code readFromNBT} dans le code source désobfusqué).</li>
     *   <li>{@code World.setTileEntity(BlockPosition, TileEntity)} — enregistre la TE.</li>
     * </ul>
     *
     * @param nmsWorld Référence au monde NMS.
     * @param ox       Offset X (coordonnée X de l'origine du collage).
     * @param oy       Offset Y.
     * @param oz       Offset Z.
     */
    private void pasteTileEntities(net.minecraft.server.v1_8_R3.World nmsWorld,
                                   int ox, int oy, int oz) {
        for (NBTTagCompound te : tileEntityData) {
            try {
                // Clone le compound pour ne pas altérer les données originales stockées
                // en mémoire (réutilisabilité du Schematic).
                NBTTagCompound clone = (NBTTagCompound) te.clone();

                // Conversion des coordonnées relatives → absolues
                clone.setInt("x", te.getInt("x") + ox);
                clone.setInt("y", te.getInt("y") + oy);
                clone.setInt("z", te.getInt("z") + oz);

                BlockPosition pos = new BlockPosition(
                        clone.getInt("x"),
                        clone.getInt("y"),
                        clone.getInt("z")
                );

                // La TileEntity doit déjà exister (créée par setTypeAndData).
                TileEntity tileEntity = nmsWorld.getTileEntity(pos);
                if (tileEntity != null) {
                    // a() = readFromNBT en v1_8_R3 (obfusqué)
                    tileEntity.a(clone);
                    nmsWorld.setTileEntity(pos, tileEntity);
                }
            } catch (Exception e) {
                Main.getInstance().debug(
                        "[Schematic] Erreur TileEntity @ ("
                                + (te.getInt("x") + ox) + ","
                                + (te.getInt("y") + oy) + ","
                                + (te.getInt("z") + oz) + ") : " + e.getMessage()
                );
            }
        }
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Getters
    // ──────────────────────────────────────────────────────────────────────────

    /** @return Nombre total de blocs (Width × Height × Length). */
    public int getTotalBlocks() { return width * height * length; }
    public Cuboid toCuboid(Location origin) {
        if (origin == null) {
            throw new IllegalArgumentException("Origin cannot be null");
        }

        if (origin.getWorld() == null) {
            throw new IllegalArgumentException("Origin world cannot be null");
        }

        final int minX = origin.getBlockX();
        final int minY = origin.getBlockY();
        final int minZ = origin.getBlockZ();

        final int maxX = minX + this.width - 1;
        final int maxY = minY + this.height - 1;
        final int maxZ = minZ + this.length - 1;

        return new Cuboid(
                origin.getWorld(),
                minX,
                minY,
                minZ,
                maxX,
                maxY,
                maxZ
        );
    }
}