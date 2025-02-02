package traben.entity_texture_features.client;


import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.passive.WolfEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

import static traben.entity_texture_features.client.entity_texture_features_CLIENT.hasUpdatableRandomCases;

public class randomCase implements entity_texture_features_METHODS {
    //Integer[] suffixes;
    //Integer[] weights;
    private final Integer[] weightedSuffixes;
    private final String[] biomes;
    private final Integer[] heights;
    private final String[] names;//add
    private final String[] professions;
    private final String[] collarColours;//add
    private final int baby; // 0 1 2 - dont true false
    private final int weather; //0,1,2,3 - no clear rain thunder
    private final String[] health;
    private final Integer[] moon;
    private final String[] daytime;


    randomCase(Integer[] suffixesX,
               Integer[] weightsX,
               String[] biomesX,
               Integer[] heightsX,
               String[] namesX,
               String[] professionsX,
               String[] collarColoursX,
               int baby012,
               int weather0123,
               String[] healthX,
               Integer[] moonX,
               String[] daytimeX
    ) {
        //Integer[] suffixes = suffixesX;
        //Integer[] weights = weightsX;
        biomes = biomesX;
        heights = heightsX;
        names = namesX;
        professions = professionsX;
        collarColours = collarColoursX;
        baby = baby012;
        weather = weather0123;
        health = healthX;
        moon = moonX;
        daytime = daytimeX;

        if (weightsX.length > 0) {
            if (weightsX.length == suffixesX.length) {
                ArrayList<Integer> buildWeighted = new ArrayList<>();
                int index = 0;
                for (int suffix :
                        suffixesX) {
                    for (int i = 0; i < weightsX[index]; i++) {
                        //adds the suffix as many times as it is weighted
                        buildWeighted.add(suffix);
                    }
                    index++;
                }
                weightedSuffixes = buildWeighted.toArray(new Integer[0]);

            } else {
                modMessage("random texture weights dont match", false);
                weightedSuffixes = suffixesX;
            }
        } else {

            weightedSuffixes = suffixesX;
        }
    }

    @Override
    public String toString() {
        return "randomCase{" +
                "weightedSuffixes=" + Arrays.toString(weightedSuffixes) +
                ", biomes=" + Arrays.toString(biomes) +
                ", heights=" + Arrays.toString(heights) +
                ", names=" + Arrays.toString(names) +
                '}';
    }

    public boolean testEntity(LivingEntity entity, boolean onlyUpdatables) {
        if (biomes.length == 0
                && names.length == 0
                && heights.length == 0
                && professions.length == 0
                && collarColours.length == 0
                && baby == 0
                && weather == 0
                && health.length == 0
                && moon.length == 0
                && daytime.length == 0
        ) {
            return true;
        } else if (//check if no updatables
                onlyUpdatables
                        && names.length == 0
                        && professions.length == 0
                        && collarColours.length == 0
                        && baby == 0
                        && health.length == 0
        ) {
            return false;//failed because no updatables but are other criteria
        }

        boolean wasTestedByUpdateable = false;
        boolean allBoolean = true;
        if (!onlyUpdatables && biomes.length > 0) {
            String entityBiome = entity.world.getBiome(entity.getBlockPos()).getCategory().getName();//has no caps// desert
            boolean check = false;
            for (String str :
                    biomes) {
                if (str.toLowerCase().equals(entityBiome)) {
                    check = true;
                    break;
                }
            }
            allBoolean = check;
        }
        if (allBoolean && names.length > 0) {
            if (entity.hasCustomName()) {
                String entityName = Objects.requireNonNull(entity.getCustomName()).getString();
                wasTestedByUpdateable = true;
                boolean check = false;
                boolean invert = false;
                for (String str :
                        names) {
                    str = str.trim();
                    if (str.startsWith("!")) {
                        str = str.replaceFirst("!", "");
                        invert = true;
                        check = true;
                    }
                    if (str.contains("regex:")) {
                        if (str.contains("iregex:")) {
                            str = str.split(":")[1];
                            if (entityName.matches("(?i)" + str)) {
                                check = !invert;
                                break;
                            }
                        } else {
                            str = str.split(":")[1];
                            if (entityName.matches(str)) {
                                check = !invert;
                                break;
                            }
                        }

                        //am i dumb? is this all pattern was????
                    } else if (str.contains("pattern:")) {
                        str = str.replace("?", ".?").replace("*", ".*");
                        if (str.contains("ipattern:")) {
                            str = str.replace("ipattern:", "");
                            if (entityName.matches("(?i)" + str)) {
                                check = !invert;
                                break;
                            }
                        } else {
                            str = str.replace("pattern:", "");
                            if (entityName.matches(str)) {
                                check = !invert;
                                break;
                            }
                        }
                    } else {//direct comparison
                        if (entityName.equals(str)) {
                            check = !invert;
                            break;
                        }
                    }

                }

                allBoolean = check;
            }else{
                allBoolean = false;
            }
        }
        if (allBoolean && !onlyUpdatables && heights.length > 0) {
            int entityHeight = entity.getBlockY();
            boolean check = false;
            for (int i :
                    heights) {
                if (i == entityHeight) {
                    check = true;
                    break;
                }
            }
            allBoolean = check;
        }
        if (allBoolean && professions.length > 0 && entity instanceof VillagerEntity) {
            wasTestedByUpdateable = true;
            String entityProfession = ((VillagerEntity) entity).getVillagerData().getProfession().toString().toLowerCase();
            int entityProfessionLevel = ((VillagerEntity) entity).getVillagerData().getLevel();
            boolean check = false;
            for (String str :
                    professions) {
                //str could be   librarian:1,3-4
                str = str.toLowerCase();
                if (str.contains(":")) {
                    String[] data = str.split(":");
                    if (entityProfession.contains(data[0]) || data[0].contains(entityProfession)) {
                        //has profession now check level
                        String[] levels = data[1].split(",");
                        ArrayList<Integer> levelData = new ArrayList<>();
                        for (String lvls :
                                levels) {
                            if (lvls.contains("-")) {
                                levelData.addAll(Arrays.asList(getIntRange(lvls)));
                            } else {
                                levelData.add(Integer.parseInt(lvls.replaceAll("[^0-9]", "")));
                            }
                        }
                        //now check levels
                        for (Integer i :
                                levelData) {
                            if (i == entityProfessionLevel) {
                                check = true;
                                break;
                            }
                        }
                    }
                } else {
                    if (entityProfession.contains(str) || str.contains(entityProfession)) {
                        check = true;
                        break;
                    }
                }
            }
            allBoolean = check;
        }
        if (allBoolean && collarColours.length > 0 && entity instanceof WolfEntity) {
            wasTestedByUpdateable = true;
            String entityCollar = ((WolfEntity) entity).getCollarColor().asString().toLowerCase();
            boolean check = false;
            for (String i :
                    collarColours) {
                i = i.toLowerCase();
                if (i.contains(entityCollar) || entityCollar.contains(i)) {
                    check = true;
                    break;
                }
            }
            allBoolean = check;
        }
        if (allBoolean && baby != 0) {
            wasTestedByUpdateable = true;
            allBoolean = (baby == 1) == entity.isBaby();
            System.out.println("baby "+allBoolean);
        }
        if (allBoolean && !onlyUpdatables && weather != 0) {
            boolean raining = entity.world.isRaining();
            boolean thundering = entity.world.isThundering();
            boolean check = false;
            if (weather == 1 && !(raining || thundering)) {
                check = true;
            } else if (weather == 2 && raining) {
                check = true;
            } else if (weather == 3 && thundering) {
                check = true;
            }
            allBoolean = check;
        }
        if (allBoolean && health.length > 0) {
            wasTestedByUpdateable = true;
            //float entityHealth = entity.getHealth();
            boolean check = false;
            //always check percentage
            float checkValue = entity.getHealth() / entity.getMaxHealth() * 100;
            for (String hlth :
                    health) {
                if (hlth.contains("-")) {
                    String[] str = hlth.split("-");
                    if (checkValue >= Integer.parseInt(str[0].replaceAll("[^0-9]", ""))
                            && checkValue <= Integer.parseInt(str[1].replaceAll("[^0-9]", ""))) {
                        check = true;
                        break;
                    }

                } else {
                    if (checkValue == Integer.parseInt(hlth.replaceAll("[^0-9]", ""))) {
                        check = true;
                        break;
                    }
                }
            }
            allBoolean = check;
        }
        if (allBoolean && !onlyUpdatables && moon.length > 0) {
            int moonPhase = entity.world.getMoonPhase();
            boolean check = false;
            for (int i :
                    moon) {
                if (i == moonPhase) {
                    check = true;
                    break;
                }
            }
            allBoolean = check;
        }
        if (allBoolean && !onlyUpdatables && daytime.length > 0) {
            long time = entity.world.getTimeOfDay();
            boolean check = false;

            for (String rangeOfTime :
                    daytime) {
                if (rangeOfTime.contains("-")) {
                    String[] str = rangeOfTime.split("-");
                    if (time >= Long.parseLong(str[0].replaceAll("[^0-9]", ""))
                            && time <= Long.parseLong(str[1].replaceAll("[^0-9]", ""))) {
                        check = true;
                        break;
                    }

                } else {
                    if (time == Long.parseLong(rangeOfTime.replaceAll("[^0-9]", ""))) {
                        check = true;
                        break;
                    }
                }
            }
            allBoolean = check;
        }
        if (wasTestedByUpdateable) {
            hasUpdatableRandomCases.put(entity.getUuid(), true);
        }
        return allBoolean;
    }

    public int getWeightedSuffix(UUID id, boolean ignoreOne) {
        int randomReliable = id.hashCode() > 0 ? id.hashCode() : -id.hashCode();
        randomReliable %= weightedSuffixes.length;
        randomReliable = weightedSuffixes[randomReliable];
        if (randomReliable == 1 && ignoreOne) randomReliable = 0;
        return randomReliable;
    }
}
