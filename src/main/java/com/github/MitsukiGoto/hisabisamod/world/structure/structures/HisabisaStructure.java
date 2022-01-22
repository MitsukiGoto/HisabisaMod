package com.github.MitsukiGoto.hisabisamod.world.structure.structures;


import com.github.MitsukiGoto.hisabisamod.HisabisaMod;
import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.jigsaw.JigsawManager;
import net.minecraft.world.gen.feature.structure.AbstractVillagePiece;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.gen.feature.structure.VillageConfig;
import net.minecraft.world.gen.feature.template.TemplateManager;

import java.util.List;

public class HisabisaStructure extends Structure<NoFeatureConfig> {

    public HisabisaStructure(Codec<NoFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public IStartFactory<NoFeatureConfig> getStartFactory() {
        return HisabisaStructure.Start::new;
    }

    @Override
    public GenerationStage.Decoration step() {
        return GenerationStage.Decoration.SURFACE_STRUCTURES;
    }

    private static final List<MobSpawnInfo.Spawners> STRUCTURE_MONSTERS = ImmutableList.of(
            new MobSpawnInfo.Spawners(EntityType.ILLUSIONER, 100, 4, 9),
            new MobSpawnInfo.Spawners(EntityType.VINDICATOR, 100, 4, 9));

    @Override
    public List<MobSpawnInfo.Spawners> getDefaultSpawnList() {
        return STRUCTURE_MONSTERS;
    }

    private static final List<MobSpawnInfo.Spawners> STRUCTURE_CREATURES = ImmutableList.of(
            new MobSpawnInfo.Spawners(EntityType.SHEEP, 30, 10, 15),
            new MobSpawnInfo.Spawners(EntityType.RABBIT, 100,1, 2)
    );

    @Override
    public List<MobSpawnInfo.Spawners> getDefaultCreatureSpawnList() {
        return STRUCTURE_CREATURES;
    }

    @Override
    protected boolean isFeatureChunk(ChunkGenerator chunkGenerator, BiomeProvider biomeSource, long seed, SharedSeedRandom chunkRandom, int chunkX, int chunkZ, Biome biome, ChunkPos chunkPos, NoFeatureConfig featureConfig) {
       BlockPos centerOfChunk = new BlockPos(chunkX * 16, 0, chunkZ * 16);
       int landHeight = chunkGenerator.getFirstOccupiedHeight(centerOfChunk.getX(), centerOfChunk.getZ(), Heightmap.Type.WORLD_SURFACE_WG);
       IBlockReader columnOfBlocks = chunkGenerator.getBaseColumn(centerOfChunk.getX(), centerOfChunk.getZ());
       BlockState topBlock = columnOfBlocks.getBlockState(centerOfChunk.above(landHeight));
       return topBlock.getFluidState().isEmpty();
    }

    public static class Start extends StructureStart<NoFeatureConfig> {

        public Start(Structure<NoFeatureConfig> structureIn, int chunkX, int chunkZ, MutableBoundingBox mutableBoundingBox, int referenceIn, long seedIn) {
            super(structureIn, chunkX, chunkZ, mutableBoundingBox, referenceIn, seedIn);
        }

        @Override
        public void generatePieces(DynamicRegistries dynamicRegistries, ChunkGenerator chunkGenerator, TemplateManager templateManagerIn, int chunkX, int chunkZ, Biome biomeIn, NoFeatureConfig config) {
            int x = chunkX * 16;
            int z = chunkZ * 16;
            BlockPos centerPos = new BlockPos(x, 0, z);
            JigsawManager.addPieces(dynamicRegistries, new VillageConfig(() -> dynamicRegistries.registryOrThrow(Registry.TEMPLATE_POOL_REGISTRY).
                            get(new ResourceLocation(HisabisaMod.MODID, "gold_structures")), 10), AbstractVillagePiece::new, chunkGenerator,
                            templateManagerIn, centerPos, this.pieces, this.random, false, true);
            this.pieces.forEach(piece -> piece.move(0, 1, 0));
        }
    }


}