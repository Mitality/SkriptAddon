package mitality.skriptAddon.elements.conditions;

import bodyhealth.api.BodyHealthAPI;
import ch.njol.skript.conditions.base.PropertyCondition;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import org.bukkit.entity.Player;
import org.skriptlang.skript.registration.SyntaxRegistry;

@Name("Can Player Sprint")
@Description("Checks whether a player is currently able to sprint according to their BodyHealth status.")
@Examples({
    "if player can sprint:",
    "if player can't sprint:"
})
@Since("1.0.0")
public class CondCanPlayerSprint extends PropertyCondition<Player> {

    public static void register(SyntaxRegistry registry) {
        registry.register(SyntaxRegistry.CONDITION,
                PropertyCondition.infoBuilder(CondCanPlayerSprint.class, PropertyType.CAN, "sprint", "players").build());
    }

    @Override
    public boolean check(Player player) {
        return BodyHealthAPI.getInstance().canPlayerSprint(player);
    }

    @Override
    protected String getPropertyName() { return "sprint"; }
}
