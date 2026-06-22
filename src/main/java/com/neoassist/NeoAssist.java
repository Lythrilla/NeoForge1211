package com.neoassist;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.logging.LogUtils;
import com.neoassist.config.ConfigManager;
import com.neoassist.gui.clickgui.ClickGuiScreen;
import com.neoassist.module.ModuleManager;
import com.neoassist.module.impl.combat.AntiKnockback;
import com.neoassist.module.impl.combat.AutoClicker;
import com.neoassist.module.impl.combat.AutoTotem;
import com.neoassist.module.impl.combat.BowAimbot;
import com.neoassist.module.impl.combat.Criticals;
import com.neoassist.module.impl.combat.KillAura;
import com.neoassist.module.impl.combat.Reach;
import com.neoassist.module.impl.misc.AntiAFK;
import com.neoassist.module.impl.misc.AutoReconnect;
import com.neoassist.module.impl.misc.FakeLag;
import com.neoassist.module.impl.misc.TimerModule;
import com.neoassist.module.impl.movement.AutoSprint;
import com.neoassist.module.impl.movement.AutoWalk;
import com.neoassist.module.impl.movement.Flight;
import com.neoassist.module.impl.movement.HighJump;
import com.neoassist.module.impl.movement.Jesus;
import com.neoassist.module.impl.movement.NoSlow;
import com.neoassist.module.impl.movement.Sneak;
import com.neoassist.module.impl.movement.Spider;
import com.neoassist.module.impl.movement.Step;
import com.neoassist.module.impl.player.AutoArmor;
import com.neoassist.module.impl.player.AutoEat;
import com.neoassist.module.impl.player.AutoFish;
import com.neoassist.module.impl.player.AutoRespawn;
import com.neoassist.module.impl.player.AutoTool;
import com.neoassist.module.impl.player.ChestStealer;
import com.neoassist.module.impl.player.FastPlace;
import com.neoassist.module.impl.player.NoFall;
import com.neoassist.module.impl.render.BlockESP;
import com.neoassist.module.impl.render.Chams;
import com.neoassist.module.impl.render.EntityESP;
import com.neoassist.module.impl.render.Fullbright;
import com.neoassist.module.impl.render.HUD;
import com.neoassist.module.impl.render.NameTags;
import com.neoassist.module.impl.render.NoHurtCam;
import com.neoassist.module.impl.render.StorageESP;
import com.neoassist.module.impl.render.Tracers;
import com.neoassist.module.impl.render.Trajectories;
import com.neoassist.module.impl.render.Zoom;
import com.neoassist.module.impl.world.AutoReplant;
import com.neoassist.module.impl.world.Nuker;
import com.neoassist.module.impl.world.Scaffold;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.ComputeFovModifierEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;

@Mod(value = NeoAssist.MOD_ID, dist = Dist.CLIENT)
public class NeoAssist {
    public static final String MOD_ID = "neoassist";
    public static final String NAME = "NeoAssist";
    public static final Logger LOGGER = LogUtils.getLogger();

    /** Right-Shift opens the ClickGUI. */
    public static final int GUI_KEY = GLFW.GLFW_KEY_RIGHT_SHIFT;

    public static ModuleManager MODULES;
    public static ConfigManager CONFIG;

    public NeoAssist(IEventBus modBus, ModContainer container) {
        LOGGER.info("{} initializing", NAME);

        MODULES = new ModuleManager();
        MODULES.register(
                // Combat
                new KillAura(), new AutoTotem(), new AutoClicker(), new AntiKnockback(),
                new Criticals(), new Reach(), new BowAimbot(),
                // Player
                new AutoEat(), new AutoTool(), new AutoArmor(), new ChestStealer(),
                new FastPlace(), new AutoRespawn(), new AutoFish(), new NoFall(),
                // Movement
                new AutoSprint(), new Step(), new Sneak(), new Spider(), new AutoWalk(), new HighJump(),
                new Flight(), new Jesus(), new NoSlow(),
                // World
                new BlockESP(), new Nuker(), new Scaffold(), new AutoReplant(),
                // Render
                new Fullbright(), new Zoom(), new HUD(), new Tracers(), new EntityESP(), new NoHurtCam(),
                new Chams(), new StorageESP(), new Trajectories(), new NameTags(),
                // Misc
                new AntiAFK(), new AutoReconnect(), new TimerModule(), new FakeLag());

        // sensible default so the overlay is visible on first launch
        MODULES.getByName("HUD").setEnabled(true);

        CONFIG = new ConfigManager();
        CONFIG.load();

        NeoForge.EVENT_BUS.addListener(this::onKey);
        NeoForge.EVENT_BUS.addListener(this::onClientTick);
        NeoForge.EVENT_BUS.addListener(this::onRenderGui);
        NeoForge.EVENT_BUS.addListener(this::onRenderLevel);
        NeoForge.EVENT_BUS.addListener(this::onFov);
        NeoForge.EVENT_BUS.addListener(this::onAttack);

        LOGGER.info("{} loaded {} modules", NAME, MODULES.getModules().size());
    }

    private void onKey(InputEvent.Key event) {
        if (event.getAction() != GLFW.GLFW_PRESS) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        if (mc.screen != null) {
            return;
        }
        int key = event.getKey();
        if (key == GUI_KEY) {
            mc.setScreen(new ClickGuiScreen());
            return;
        }
        MODULES.onKey(key);
    }

    private void onClientTick(ClientTickEvent.Post event) {
        if (Minecraft.getInstance().player != null) {
            MODULES.onTick();
        }
    }

    private void onRenderGui(RenderGuiEvent.Post event) {
        MODULES.onRender2D(event.getGuiGraphics(), 0.0F);
    }

    private void onRenderLevel(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        PoseStack poseStack = event.getPoseStack();
        Vec3 cameraPos = event.getCamera().getPosition();
        float partial = event.getPartialTick().getGameTimeDeltaPartialTick(false);
        MultiBufferSource.BufferSource buffer = mc.renderBuffers().bufferSource();
        MODULES.onWorldRender(poseStack, buffer, cameraPos, partial);
        buffer.endBatch(RenderType.lines());
    }

    private void onFov(ComputeFovModifierEvent event) {
        MODULES.onFov(event);
    }

    private void onAttack(AttackEntityEvent event) {
        MODULES.onAttack(event.getTarget());
    }
}
