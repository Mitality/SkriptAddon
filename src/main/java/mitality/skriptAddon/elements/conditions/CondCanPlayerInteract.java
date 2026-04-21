package mitality.skriptAddon.elements.conditions;

import bodyhealth.api.BodyHealthAPI;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;

@Name("Can Player Interact")
@Description("Checks whether a player is currently able to interact with their main hand or off hand according to their BodyHealth status.")
@Examples({
    "if player can interact with main hand:",
    "if player can't interact with off hand:"
})
@Since("1.0.0")
public class CondCanPlayerInteract extends Condition {

    public static final String[] PATTERNS = {
        "%players% can interact with [their] (main[ ]hand|primary hand)",
        "%players% can interact with [their] (off[ ]hand|secondary hand)",
        "%players% (can't|cannot) interact with [their] (main[ ]hand|primary hand)",
        "%players% (can't|cannot) interact with [their] (off[ ]hand|secondary hand)"
    };

    public static void register(SyntaxRegistry registry) {
        registry.register(SyntaxRegistry.CONDITION, SyntaxInfo.builder(CondCanPlayerInteract.class)
                .addPatterns(PATTERNS)
                .build());
    }

    private int pattern;
    private Expression<Player> players;

    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        pattern = matchedPattern;
        players = (Expression<Player>) exprs[0];
        setNegated(pattern >= 2);
        return true;
    }

    @Override
    public boolean check(Event event) {
        EquipmentSlot hand = (pattern == 0 || pattern == 2) ? EquipmentSlot.HAND : EquipmentSlot.OFF_HAND;
        BodyHealthAPI api = BodyHealthAPI.getInstance();
        return players.check(event, p -> api.canPlayerInteract(p, hand), isNegated());
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        String hand = (pattern == 0 || pattern == 2) ? "main hand" : "off hand";
        return players.toString(event, debug) + (isNegated() ? " cannot" : " can") + " interact with " + hand;
    }
}
