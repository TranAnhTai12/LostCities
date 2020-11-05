package mcjty.lostcities.worldgen;

import mcjty.lostcities.config.LostCityProfile;
import mcjty.lostcities.worldgen.lost.cityassets.AssetRegistries;
import mcjty.lostcities.worldgen.lost.cityassets.WorldStyle;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.chunk.AbstractChunkProvider;
import net.minecraft.world.server.ServerChunkProvider;

import java.util.Random;

public class DefaultDimensionInfo implements IDimensionInfo {

    private ISeedReader world;
    private final LostCityProfile profile;
    private final WorldStyle style;

    private final Registry<Biome> biomeRegistry;
    private final LostCityTerrainFeature feature;

    public DefaultDimensionInfo(ISeedReader world, LostCityProfile profile) {
        this.world = world;
        this.profile = profile;
        style = AssetRegistries.WORLDSTYLES.get("standard");
        feature = new LostCityTerrainFeature(this, profile, getRandom());
        feature.setupStates(profile);
        biomeRegistry = DynamicRegistries.func_239770_b_().func_230521_a_(Registry.BIOME_KEY).get();
    }

    @Override
    public void setWorld(ISeedReader world) {
        this.world = world;
    }

    @Override
    public long getSeed() {
        return world.getSeed();
    }

    @Override
    public ISeedReader getWorld() {
        return world;
    }

    @Override
    public RegistryKey<World> getType() {
        return world.getWorld().getDimensionKey();
    }

    @Override
    public LostCityProfile getProfile() {
        return profile;
    }

    @Override
    public LostCityProfile getOutsideProfile() {
        return null;
    }

    @Override
    public WorldStyle getWorldStyle() {
        return style;
    }

    @Override
    public Random getRandom() {
        return world.getRandom();
    }

    @Override
    public LostCityTerrainFeature getFeature() {
        return feature;
    }

    @Override
    public ChunkHeightmap getHeightmap(int chunkX, int chunkZ) {
        return feature.getHeightmap(chunkX, chunkZ, getWorld());
    }

//    @Override
//    public Biome[] getBiomes(int chunkX, int chunkZ) {
//        AbstractChunkProvider chunkProvider = getWorld().getChunkProvider();
//        if (chunkProvider instanceof ServerChunkProvider) {
//            BiomeProvider biomeProvider = ((ServerChunkProvider) chunkProvider).getChunkGenerator().getBiomeProvider();
//            return biomeProvider.getBiomes((chunkX - 1) * 4 - 2, chunkZ * 4 - 2, 10, 10, false);
//        }
//    }
//
    @Override
    public Biome getBiome(BlockPos pos) {
        AbstractChunkProvider chunkProvider = getWorld().getChunkProvider();
        if (chunkProvider instanceof ServerChunkProvider) {
            BiomeProvider biomeProvider = ((ServerChunkProvider) chunkProvider).getChunkGenerator().getBiomeProvider();
            // @todo 1.15 check if this is correct!
            return biomeProvider.getNoiseBiome(pos.getX(), pos.getY(), pos.getZ());
        }
        // @todo 1.16: is this right
        return biomeRegistry.getOptional(Biomes.PLAINS.getLocation()).get();
    }
}
