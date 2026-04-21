package mitality.skriptAddon.elements.conditions;

import bodyhealth.api.BodyHealthAPI;
import ch.njol.skript.conditions.base.PropertyCondition;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import org.bukkit.entity.Player;
import org.skriptlang.skript.registration.SyntaxRegistry;

@Name("Is Vanished")
@Description("Checks whether a player is vanished via PremiumVanish or SuperVanish.")
@Examples({
    "if player is vanished:",
    "if player isn't vanished:"
})
@Since("1.0.0")
public class CondIsVanished extends PropertyCondition<Player> {

    public static void register(SyntaxRegistry registry) {
        registry.register(SyntaxRegistry.CONDITION,
                PropertyCondition.infoBuilder(CondIsVanished.class, PropertyType.BE, "vanished", "players").build());
    }

    @Override
    public boolean check(Player player) {
        return BodyHealthAPI.getInstance().isVanished(player);
    }

    @Override
    protected String getPropertyName() { return "vanished"; }
}
