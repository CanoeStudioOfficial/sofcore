package com.canoestudio.sofcore;

import net.minecraftforge.common.config.Config;

@Config(modid = SOFcore.MOD_ID)
@Config.LangKey("sofcore.config.title")
public class SOFConfig {

    @Config.Name("join_warning")
    @Config.LangKey("sofcore.config.join_warning")
    @Config.Comment("Controls the warning sent to players when they join a world/server.")
    public static JoinWarning joinWarning = new JoinWarning();

    @Config.Name("dynamic_trees")
    @Config.LangKey("sofcore.config.dynamic_trees")
    @Config.Comment("Compatibility fixes for Dynamic Trees.")
    public static DynamicTrees dynamicTrees = new DynamicTrees();

    public static class JoinWarning {

        @Config.Name("enabled")
        @Config.LangKey("sofcore.config.join_warning.enabled")
        @Config.Comment("Set to true to send the configured warning when a player joins.")
        public boolean enabled = false;

        @Config.Name("stage")
        @Config.LangKey("sofcore.config.join_warning.stage")
        @Config.Comment("Select which warning text should be sent.")
        public PackStage stage = PackStage.SNAPSHOT;
    }

    public static class DynamicTrees {

        @Config.Name("destroy_explosion_roots")
        @Config.LangKey("sofcore.config.dynamic_trees.destroy_explosion_roots")
        @Config.Comment("When Dynamic Trees are destroyed by explosions, remove their rooty dirt after the explosion finishes.")
        public boolean destroyExplosionRoots = true;
    }

    public enum PackStage {
        SNAPSHOT("sofcore.join_warning.snapshot"),
        PRE("sofcore.join_warning.pre");

        private final String langKey;

        PackStage(String langKey) {
            this.langKey = langKey;
        }

        public String getLangKey() {
            return langKey;
        }
    }
}
