package mitality.skriptAddon;

import bodyhealth.api.addons.AddonInfo;
import bodyhealth.api.addons.BodyHealthAddon;
import ch.njol.skript.Skript;
import mitality.skriptAddon.elements.conditions.*;
import mitality.skriptAddon.elements.effects.*;
import mitality.skriptAddon.elements.events.*;
import mitality.skriptAddon.elements.expressions.*;
import mitality.skriptAddon.elements.types.SkriptTypes;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.skriptlang.skript.addon.SkriptAddon;
import org.skriptlang.skript.bukkit.lang.eventvalue.EventValueRegistry;
import org.skriptlang.skript.registration.SyntaxRegistry;

@AddonInfo(
        name = "SkriptAddon",
        description = "Use BodyHealth with Skript",
        version = "1.0.0",
        author = "Mitality"
)
public final class Main extends BodyHealthAddon {

    private static Main instance;

    public static Main getInstance() {
        return instance;
    }

    @Override
    public void onAddonEnable() {
        instance = this;

        getAddonDebug().log("Enabling SkriptAddon v" + getAddonInfo().version() + "...");

        if (!Bukkit.getServer().getPluginManager().isPluginEnabled("Skript")) {
            getAddonDebug().logErr("Skript not found! SkriptAddon requires Skript to function.");
            return;
        }

        SkriptAddon addon = Skript.instance().registerAddon(getBodyHealthPlugin().getClass(), "SkriptAddon");
        SyntaxRegistry registry = addon.syntaxRegistry();
        EventValueRegistry eventRegistry = addon.registry(EventValueRegistry.class,
                () -> EventValueRegistry.empty(JavaPlugin.getPlugin(Skript.class)));

        SkriptTypes.register();

        EffHealPlayer.register(registry);
        EffDamagePlayerDirectly.register(registry);
        EffDamagePlayerWithConfig.register(registry);
        EffSetBodyHealth.register(registry);
        EffValidateEffects.register(registry);

        ExprBodyPartHealth.register(registry);
        ExprBodyPartState.register(registry);
        ExprMaxPartHealth.register(registry);

        CondBodyHealthEnabled.register(registry);
        CondCanPlayerJump.register(registry);
        CondCanPlayerWalk.register(registry);
        CondCanPlayerSprint.register(registry);
        CondCanPlayerInteract.register(registry);
        CondIsVanished.register(registry);

        EvtBodyPartHealthChange.register(registry, eventRegistry);
        EvtBodyPartStateChange.register(registry, eventRegistry);

        getAddonDebug().log("SkriptAddon v" + getAddonInfo().version() + " enabled.");
    }

    @Override
    public void onAddonDisable() {
        instance = null;
    }
}
