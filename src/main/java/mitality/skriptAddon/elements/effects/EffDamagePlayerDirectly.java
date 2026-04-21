package mitality.skriptAddon.elements.effects;

import bodyhealth.api.BodyHealthAPI;
import bodyhealth.core.BodyPart;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;

@Name("Damage Body Part Directly")
@Description("Directly damages a player's body parts by a specific amount, ignoring the plugin's damage configuration.")
@Examples({
    "directly damage player by 10",
    "directly damage right leg of player by 5.5"
})
@Since("1.0.0")
public class EffDamagePlayerDirectly extends Effect {

    public static final String[] PATTERNS = {
        "directly damage %players% by %number%",
        "directly damage %bodypart% of %players% by %number%"
    };

    public static void register(SyntaxRegistry registry) {
        registry.register(SyntaxRegistry.EFFECT, SyntaxInfo.builder(EffDamagePlayerDirectly.class)
                .addPatterns(PATTERNS)
                .build());
    }

    private int pattern;
    private Expression<Player> players;
    private Expression<BodyPart> bodyPart;
    private Expression<Number> amount;

    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        pattern = matchedPattern;
        if (pattern == 0) {
            players = (Expression<Player>) exprs[0];
            amount = (Expression<Number>) exprs[1];
        } else {
            bodyPart = (Expression<BodyPart>) exprs[0];
            players = (Expression<Player>) exprs[1];
            amount = (Expression<Number>) exprs[2];
        }
        return true;
    }

    @Override
    protected void execute(Event event) {
        Number amt = amount.getSingle(event);
        if (amt == null) return;
        BodyHealthAPI api = BodyHealthAPI.getInstance();
        for (Player player : players.getArray(event)) {
            if (pattern == 0) {
                api.damagePlayerDirectly(player, amt.doubleValue());
            } else {
                BodyPart part = bodyPart.getSingle(event);
                if (part != null) api.damagePlayerDirectly(player, amt.doubleValue(), part);
            }
        }
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        if (pattern == 0) {
            return "directly damage " + players.toString(event, debug) + " by " + amount.toString(event, debug);
        }
        return "directly damage " + bodyPart.toString(event, debug) + " of " + players.toString(event, debug) + " by " + amount.toString(event, debug);
    }
}
