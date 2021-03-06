package mods.flammpfeil.slashblade.stats;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import mods.flammpfeil.slashblade.gui.AchievementsExtendedGuiHandler;
import mods.flammpfeil.slashblade.item.ItemProudSoul;
import net.minecraft.init.Enchantments;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import mods.flammpfeil.slashblade.RecipeWrapBlade;
import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.util.SlashBladeAchievementCreateEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.IStatStringFormat;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatList;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.AchievementPage;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.world.WorldEvent;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Furia on 14/11/17.
 */
public class AchievementList {

    public final static AchievementList INSTANCE = new AchievementList();

    static public Map<String,String> craftingTrigger = Maps.newHashMap();

    static public Map<String,Achievement> achievements = Maps.newHashMap();


    static public Map<String,Integer> achievementIcons = Maps.newHashMap();

    static{
        String[] icons = {
                "rank_a","rank_b","rank_c","rank_d","rank_s","rank_ss"
                ,"slash","soul_eater","hundred_kill","stand"};
        int achievementIconId = ItemProudSoul.AchievementIconIdHead;
        for(String s : icons) {
            int id = achievementIconId++;
            achievementIcons.put(s,id);
        }
        String[] effectIcons = {"rank_sss","thousand_kill"};
        int achievementEffectedIconId = ItemProudSoul.AchievementEffectedIconIdHead;
        for(String s : effectIcons) {
            int id = achievementEffectedIconId++;
            achievementIcons.put(s,id);
        }
    }

    static public ItemStack getIconStack(String name){
        ItemStack stack = SlashBlade.getCustomBlade("proudsoul");
        stack.setItemDamage(achievementIcons.get(name));
        return stack;
    }

    static public void setContent(Achievement achievement,String contentKey){
        if(!(achievement instanceof AchievementEx))
            return;
        if(!SlashBlade.recipeMultimap.containsKey(contentKey))
            return;

        ((AchievementEx) achievement).content =  Lists.newArrayList(SlashBlade.recipeMultimap.get(contentKey));
    }

    static public void init(){
        MinecraftForge.EVENT_BUS.register(INSTANCE);

        //Achievement parent;
        Stack<Achievement> parent = new Stack<Achievement>();

        {
            parent.push(registerAchievement("slashWoodenSword", getIconStack("slash"), net.minecraft.stats.AchievementList.BUILD_SWORD).initIndependentStat());
            {
                parent.push(registerCraftingAchievement("buildWoodenBlade", SlashBlade.getCustomBlade("slashbladeWood"), parent.peek()));
                {
                    setContent(parent.peek(),"slashbladeWood");

                    parent.push(registerCraftingAchievement("takemitu", SlashBlade.getCustomBlade("slashbladeBambooLight"), parent.peek()));
                    {
                        setContent(parent.peek(),"slashbladeBambooLight");

                        parent.push(registerCraftingAchievement("ginsitakemitu", SlashBlade.getCustomBlade("slashbladeSilverBambooLight"), parent.peek()));
                        {
                            setContent(parent.peek(),"slashbladeSilverBambooLight");

                            parent.push(registerAchievement("saya", SlashBlade.getCustomBlade("slashbladeWrapper"), parent.peek()));
                            {
                                /*
                                parent.push(registerCraftingAchievement("bamboo", SlashBlade.getCustomBlade("wrap.BambooMod.katana.sample"), parent.peek()));
                                {
                                    setContent(parent.peek(),"wrap.BambooMod.katana.sample");

                                    parent.push(registerCraftingAchievement("foxwhite", SlashBlade.getCustomBlade("flammpfeil.slashblade.named.fox.white"), parent.peek()).setSpecial());
                                    setContent(parent.peek(),"flammpfeil.slashblade.named.fox.white");
                                    parent.pop();

                                    parent.push(registerCraftingAchievement("foxblack", SlashBlade.getCustomBlade("flammpfeil.slashblade.named.fox.black"), parent.peek()).setSpecial());
                                    setContent(parent.peek(), "flammpfeil.slashblade.named.fox.black");
                                    parent.pop();
                                }
                                parent.pop();

                                for(Map.Entry<String,String> entry : RecipeWrapBlade.wrapableTextureNames.entrySet()){
                                    String[] modid = entry.getKey().split(":");
                                    if(modid.length == 2 && modid[0].length() != 0 && Loader.isModLoaded(modid[0])){
                                        AchievementList.registerCraftingAchievement("wrap." + entry.getValue(), RecipeWrapBlade.getWrapSampleBlade(entry.getKey(), entry.getValue()), parent.peek());
                                    }
                                }
                                */
                            }
                            parent.pop();
                        }
                        parent.pop();
                    }
                    parent.pop();

                    parent.push(registerCraftingAchievement("tagayasan", SlashBlade.getCustomBlade("flammpfeil.slashblade.named.tagayasan"), parent.peek()).setSpecial());
                    setContent(parent.peek(), "flammpfeil.slashblade.named.tagayasan");
                    parent.pop();

                    parent.push(registerCraftingAchievement("buildWhiteSheath", SlashBlade.getCustomBlade("slashbladeWhite"), parent.peek()));
                    {
                        setContent(parent.peek(), "slashbladeWhite");

                        parent.push(registerAchievement("brokenWhiteSheath", SlashBlade.getCustomBlade("BrokenBladeWhite"), parent.peek()).setSpecial());
                        {
                            parent.push(registerCraftingAchievement("buildSlashBlade", SlashBlade.getCustomBlade("slashblade"), parent.peek()));
                            setContent(parent.peek(), "slashblade");


                            ItemStack blade = SlashBlade.getCustomBlade("slashblade");
                            blade.addEnchantment(Enchantments.FIRE_ASPECT, 1);
                            {
                                parent.push(registerAchievement("enchanted", blade, parent.peek()));
                                {
                                    parent.push(registerAchievement("bewitched", blade, parent.peek()).setSpecial());
                                    parent.pop();
                                }
                                parent.pop();

                                parent.push(registerCraftingAchievement("muramasa", SlashBlade.getCustomBlade("flammpfeil.slashblade.named.muramasa"), parent.peek()).setSpecial());
                                setContent(parent.peek(), "flammpfeil.slashblade.named.muramasa");
                                parent.pop();

                                parent.push(registerCraftingAchievement("tukumo", SlashBlade.getCustomBlade("flammpfeil.slashblade.named.yuzukitukumo"), parent.peek()).setSpecial());
                                setContent(parent.peek(), "flammpfeil.slashblade.named.yuzukitukumo");
                                parent.pop();
                            }
                            parent.pop();
                        }
                        parent.pop();
                    }
                    parent.pop();
                }
                parent.pop();
            }

            {
                parent.push(registerCraftingAchievement("noname", SlashBlade.getCustomBlade("slashbladeNamed"), parent.peek()));
                setContent(parent.peek(), "slashbladeNamed");
                parent.pop();
            }

            {
                parent.push(registerAchievement("hundredKill", getIconStack("hundred_kill"), parent.peek()));
                {
                    parent.push(registerAchievement("thousandKill", getIconStack("thousand_kill"), parent.peek()).setSpecial());
                    parent.pop();
                }
                parent.pop();
            }

            {
                parent.push(registerAchievement("soulEater", getIconStack("soul_eater"), parent.peek()));
                parent.pop();
            }

            {
                parent.push(registerAchievement("proudSoul", SlashBlade.getCustomBlade("proudsoul"), parent.peek()));
                {
                    setContent(parent.peek(), "proudsoul");



                    parent.push(registerCraftingAchievement("soulCrystal", SlashBlade.getCustomBlade("crystal_bladesoul"), parent.peek()));
                    setContent(parent.peek(), "crystal_bladesoul");
                    {
                        parent.push(registerCraftingAchievement("namedbladeSoul", SlashBlade.getCustomBlade("crystal_bladesoul"), parent.peek()).setSpecial());
                        setContent(parent.peek(), "namedblade_soul");
                        parent.pop();

                        parent.push(registerCraftingAchievement("soulTrapezohedron", SlashBlade.getCustomBlade("trapezohedron_bladesoul"), parent.peek()).setSpecial());
                        setContent(parent.peek(), "trapezohedron_bladesoul");
                        parent.pop();
                    }
                    parent.pop();

                    parent.push(registerCraftingAchievement("tinySoul", SlashBlade.getCustomBlade("tiny_bladesoul"), parent.peek()));
                    setContent(parent.peek(), "tiny_bladesoul");
                    parent.pop();

                    parent.push(registerCraftingAchievement("soulIngot", SlashBlade.getCustomBlade("ingot_bladesoul"), parent.peek()));
                    {
                        setContent(parent.peek(), "ingot_bladesoul");

                        parent.push(registerCraftingAchievement("soulSphere", SlashBlade.getCustomBlade("sphere_bladesoul"), parent.peek()).setSpecial());
                        setContent(parent.peek(), "sphere_bladesoul");
                        parent.pop();
                    }
                    parent.pop();

                    parent.push(registerAchievement("enchantmentSoul", SlashBlade.getCustomBlade("tiny_bladesoul"), parent.peek()).setSpecial());
                    parent.pop();

                    parent.push(registerAchievement("bladeStand", getIconStack("stand"), parent.peek()));
                    parent.pop();


                    parent.push(registerAchievement("phantomSword", new ItemStack(Items.DIAMOND_SWORD), parent.peek()));
                    {
                        parent.push(registerAchievement("phantomBlade", new ItemStack(Items.FEATHER), parent.peek()));
                        parent.pop();
                    }
                    parent.pop();
                }
                parent.pop();
            }

            {
                parent.push(registerAchievement("rankD", getIconStack("rank_d"), parent.peek()));
                {
                    parent.push(registerAchievement("rankC", getIconStack("rank_c"), parent.peek()));
                    {
                        parent.push(registerAchievement("rankB", getIconStack("rank_b"), parent.peek()));
                        {
                            parent.push(registerAchievement("rankA", getIconStack("rank_a"), parent.peek()));
                            {
                                parent.push(registerAchievement("rankS", getIconStack("rank_s"), parent.peek()));
                                {
                                    parent.push(registerAchievement("rankSS", getIconStack("rank_ss"), parent.peek()));
                                    {
                                        parent.push(registerAchievement("rankSSS", getIconStack("rank_sss"), parent.peek()).setSpecial());
                                        parent.pop();
                                    }
                                    parent.pop();
                                }
                                parent.pop();
                            }
                            parent.pop();
                        }
                        parent.pop();
                    }
                    parent.pop();
                }
                parent.pop();
            }

            parent.pop();
        }

        {
            parent.push(registerCraftingAchievement("brokenYamato", SlashBlade.getCustomBlade("flammpfeil.slashblade.named.yamato.broken"), net.minecraft.stats.AchievementList.THE_END));
            {
                parent.push(registerCraftingAchievement("yamato", SlashBlade.getCustomBlade("flammpfeil.slashblade.named.yamato"), parent.peek()).setSpecial());
                setContent(parent.peek(), "flammpfeil.slashblade.named.yamato");
                parent.pop();
            }
            parent.pop();
        }

        {
            parent.push(registerCraftingAchievement("sabigatana", SlashBlade.getCustomBlade("flammpfeil.slashblade.named.sabigatana.broken"), net.minecraft.stats.AchievementList.KILL_ENEMY));
            {
                setContent(parent.peek(), "flammpfeil.slashblade.named.sabigatana");

                parent.push(registerCraftingAchievement("doutanuki", SlashBlade.getCustomBlade("flammpfeil.slashblade.named.doutanuki"), parent.peek()));
                setContent(parent.peek(), "flammpfeil.slashblade.named.doutanuki");
                parent.pop();
            }
            parent.pop();
        }


        {
            Achievement startParent = net.minecraft.stats.AchievementList.KILL_ENEMY;
            /*if(Loader.isModLoaded("TwilightForest"))*/{
                StatBase stat = StatList.getOneShotStat("TwilightForest6"); //twilightKillNaga
                if(stat != null && stat instanceof  Achievement){
                    startParent = (Achievement) stat;
                }

                parent.push(registerCraftingAchievement("agito.rust", SlashBlade.getCustomBlade("flammpfeil.slashblade.named.agito.rust"), startParent));
                {
                    parent.push(registerCraftingAchievement("agito", SlashBlade.getCustomBlade("flammpfeil.slashblade.named.agito"), parent.peek()));
                    setContent(parent.peek(), "flammpfeil.slashblade.named.agito");
                    parent.pop();
                }
                parent.pop();
            }
        }


        {
            Achievement startParent = net.minecraft.stats.AchievementList.KILL_ENEMY;
            /*if(Loader.isModLoaded("TwilightForest"))*/{
                StatBase stat = StatList.getOneShotStat("TwilightForest30"); //twilightKillHydra
                if(stat != null && stat instanceof  Achievement){
                    startParent = (Achievement) stat;
                }

                parent.push(registerCraftingAchievement("orotiagito.rust", SlashBlade.getCustomBlade("flammpfeil.slashblade.named.orotiagito.rust"), startParent));
                {
                    parent.push(registerCraftingAchievement("orotiagito.sealed", SlashBlade.getCustomBlade("flammpfeil.slashblade.named.orotiagito.seald"), parent.peek()));
                    {
                        setContent(parent.peek(), "flammpfeil.slashblade.named.orotiagito.seald");

                        parent.push(registerCraftingAchievement("orotiagito", SlashBlade.getCustomBlade("flammpfeil.slashblade.named.orotiagito"), parent.peek()).setSpecial());
                        setContent(parent.peek(), "flammpfeil.slashblade.named.orotiagito");
                        parent.pop();
                    }
                    parent.pop();
                }
                parent.pop();
            }
        }

        {
            Achievement startParent = net.minecraft.stats.AchievementList.KILL_ENEMY;
            /*if(Loader.isModLoaded("TwilightForest"))*/{
                StatBase stat = StatList.getOneShotStat("TwilightForest6"); //twilightKillNaga
                if(stat != null && stat instanceof  Achievement){
                    startParent = (Achievement) stat;
                }

                parent.push(registerCraftingAchievement("yasha", SlashBlade.getCustomBlade("flammpfeil.slashblade.named.yasha"), startParent));
                parent.pop();

                parent.push(registerCraftingAchievement("yashatrue", SlashBlade.getCustomBlade("flammpfeil.slashblade.named.yashatrue"), startParent));
                parent.pop();
            }
        }

        {
            Achievement startParent = net.minecraft.stats.AchievementList.KILL_WITHER;
            parent.push(registerCraftingAchievement("sange", SlashBlade.getCustomBlade("flammpfeil.slashblade.named.sange"), startParent));
            parent.pop();
        }

        SlashBlade.InitEventBus.post(new SlashBladeAchievementCreateEvent());

        AchievementPage.registerAchievementPage(new AchievementPage(SlashBlade.modname, achievements.values().toArray(new Achievement[]{})) {
            @Override
            public String getName() {
                if(AchievementsExtendedGuiHandler.nowTargetGui)
                    return super.getName();
                else
                    return I18n.translateToLocal("flammpfeil.slashblade");//super.getName());
    }
        });
    }

    static int defaultCounter = 0;
    static int defaultX = -10;
    //static int minX = 0;
    static int defaultY = 10;
    static int minY = -6;
    static public Pattern PosPattern = Pattern.compile("(-?\\d+)\\s*,\\s*(-?\\d+)");
    static public Achievement registerAchievement(String key, ItemStack icon, Achievement parent){
        String translateKey = getTranslateKey(key);
        int x = defaultX;
        int y = defaultY;

        String posStr = I18n.translateToLocal("achievement." + translateKey + ".pos");


        Matcher mat = PosPattern.matcher(posStr.trim());
        if(mat.matches()){
            x = Integer.parseInt(mat.group(1));
            //x = Math.max(minX,x);
            y = Integer.parseInt(mat.group(2));
            //y = Math.max(minY,y);
        }else{
            x = defaultX + (int)(defaultCounter / 5);
            y = defaultY + defaultCounter % 5;
            defaultCounter++;
        }

        Achievement achievement = registerAchievement(key, x, y, icon, parent);
        return achievement;
    }
    static public Achievement registerAchievement(String key, int x, int y, ItemStack icon, Achievement parent){
        Achievement achievement = getAchievement(key);
        if(achievement == null) {
            achievement = new AchievementEx(getAchievementKey(key), getTranslateKey(key), x, y, icon, parent);

            achievements.put(achievement.statId, achievement);

            achievement.registerStat();
        }

        return achievement;
    } 
    static public Achievement registerCraftingAchievement(Achievement achievement){
        craftingTrigger.put(achievement.theItemStack.getUnlocalizedName(), achievement.statId);
        return achievement;
    }
    static public Achievement registerCraftingAchievement(String key, ItemStack icon, Achievement parent){
        Achievement achievement = registerAchievement(key, icon, parent);
        return registerCraftingAchievement(achievement);
    }
    static public Achievement registerCraftingAchievement(String key, int x, int y, ItemStack icon, Achievement parent){
        Achievement achievement = registerAchievement(key, x, y, icon, parent);
        return registerCraftingAchievement(achievement);
    }

    static public Achievement getAchievement(String key){
        if(key.indexOf(":") < 0){
            key = getAchievementKey(key);
        }
        Achievement achievement = achievements.get(key);
        return achievement;
    }

    static public void triggerAchievement(EntityPlayer player, String key){
        Achievement achievement = getAchievement(key);
        if(achievement != null)
            player.addStat(achievement);
    }

    static public void triggerCraftingAchievement(ItemStack stack, EntityPlayer player) {
        if(craftingTrigger.containsKey(stack.getUnlocalizedName())){
            triggerAchievement(player, craftingTrigger.get(stack.getUnlocalizedName()));
        }
    }

    static boolean isLoaded = false;
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void worldLoadEvent(WorldEvent.Load event){
        if(!isLoaded){
            isLoaded = true;

            IStatStringFormat formatter = new IStatStringFormat() {
                @Override
                public String formatString(String p_74535_1_) {
                    p_74535_1_= p_74535_1_.replaceAll("<br>","\n");
                    return p_74535_1_;
                }
            };

            for(Achievement ach : achievements.values()){
                ach.setStatStringFormatter(formatter);
            }
        }
    }

    @SubscribeEvent
    public void playerEventItemPickupEvent(PlayerEvent.ItemPickupEvent event){
        triggerCraftingAchievement(event.pickedUp.getItem(),event.player);
    }

    @SubscribeEvent
    public void livingAttackEvent(LivingAttackEvent event){
        DamageSource src = event.getSource();
        if(src.getDamageType() != "player") return;

        Entity e = src.getEntity();
        if(e == null) return;

        if(!(e instanceof EntityPlayer))return;

        ItemStack item = ((EntityPlayer) e).getHeldItem(EnumHand.MAIN_HAND);
        if(item ==null) return;
        if(item.getItem() != Items.WOODEN_SWORD) return;
        if(item.getItemDamage() != 0) return;

        triggerAchievement((EntityPlayer)e,"slashWoodenSword");
    }

    static public String getAchievementKey(String key){
        return SlashBlade.modid + ":achievement." + key;
    }
    static public String getTranslateKey(String key){
        return SlashBlade.modid + "." + key;
    }

    static public AchievementEx currentMouseOver = null;

}
