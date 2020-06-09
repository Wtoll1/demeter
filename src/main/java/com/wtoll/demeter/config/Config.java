package com.wtoll.demeter.config;

import com.google.gson.*;

import java.io.*;

/**
 * @author <Wtoll> Will Toll on 2020-05-28
 * @project Demeter
 */
public class Config {
    public static final String CONFIG_LOCATION = System.getProperty("user.dir") + "/config/demeter/config.json";

    public static final Config DEFAULT = new Config(0.05f, 0.05f, 0.05f, 0.9f, true, 0.5f, 1);

    private float spreadProbability;
    private float mutationProbability;
    private float breedProbability;
    private float maxGrowthProbability;
    private boolean autoModifyLootTables;
    private float lootTableBinomProbability;
    private int spreadRadius;

    public Config(float spreadProbability, float mutationProbability, float breedProbability, float maxGrowthProbability, boolean autoModifyLootTables, float lootTableBinomProbability, int spreadRadius) {
        this.spreadProbability = spreadProbability;
        this.mutationProbability = mutationProbability;
        this.breedProbability = breedProbability;
        this.maxGrowthProbability = maxGrowthProbability;
        this.autoModifyLootTables = autoModifyLootTables;
        this.lootTableBinomProbability = lootTableBinomProbability;
        this.spreadRadius = spreadRadius;
    }

    public float getSpreadProbability() {
        return spreadProbability;
    }

    public float getMutationProbability() {
        return mutationProbability;
    }

    public float getBreedProbability() {
        return breedProbability;
    }

    public float getMaxGrowthProbability() {
        return maxGrowthProbability;
    }

    public boolean getAutoModifyLootTables() {
        return autoModifyLootTables;
    }

    public float getLootTableBinomProbability() {
        return lootTableBinomProbability;
    }

    public int getSpreadRadius() {
        return spreadRadius;
    }

    public static Config load() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try {
            Object json = gson.fromJson(new FileReader(CONFIG_LOCATION), Config.class);
            return (Config) json;
        } catch (FileNotFoundException|JsonSyntaxException e) {
            File file = new File(CONFIG_LOCATION);
            File dir = new File(file.getParent());
            if (!dir.exists()) {
                dir.mkdir();
            }
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            try {
                Writer writer = new FileWriter(CONFIG_LOCATION);
                gson.toJson(DEFAULT, writer);
                writer.close();
            } catch (JsonIOException|IOException e1) {
                e1.printStackTrace();
            }
            return DEFAULT;
        }
    }
}
