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
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;

@Name("Damage Body Part With Config")
@Description("Damages a player's body parts using the BodyHealth damage configuration for the given damage cause.")
@Examples({
    "damage player by 8 with cause fall damage",
    "damage torso of player by 12 with cause fire"
})
@Since("1.0.0")
public class EffDamagePlayerWithConfig extends Effect {

    public static final String[] PATTERNS = {
        "damage %players% by %number% with [damage] cause %damagecause%",
        "damage %bodypart% of %players% by %number% with [damage] cause %damagecause%"
    };

    public static void register(SyntaxRegistry registry) {
        registry.register(SyntaxRegistry.EFFECT, SyntaxInfo.builder(EffDamagePlayerWithConfig.class)
                .addPatterns(PATTERNS)
                .build());
    }

    private int pattern;
    private Expression<Player> players;
    private Expression<BodyPart> bodyPart;
    private Expression<Number> amount;
    private Expression<EntityDamageEvent.DamageCause> damageCause;

    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        pattern = matchedPattern;
        if (pattern == 0) {
            players = (Expression<Player>) exprs[0];
            amount = (Expression<Number>) exprs[1];
            damageCause = (Expression<EntityDamageEvent.DamageCause>) exprs[2];
        } else {
            bodyPart = (Expression<BodyPart>) exprs[0];
            players = (Expression<Player>) exprs[1];
            amount = (Expression<Number>) exprs[2];
            damageCause = (Expression<EntityDamageEvent.DamageCause>) exprs[3];
        }
        return true;
    }

    @Override
    protected void execute(Event event) {
        Number amt = amount.getSingle(event);
        EntityDamageEvent.DamageCause cause = damageCause.getSingle(event);
        if (amt == null || cause == null) return;
        BodyHealthAPI api = BodyHealthAPI.getInstance();
        for (Player player : players.getArray(event)) {
            if (pattern == 0) {
                api.damagePlayerWithConfig(player, cause, amt.doubleValue());
            } else {
                BodyPart part = bodyPart.getSingle(event);
                if (part != null) api.damagePlayerWithConfig(player, cause, amt.doubleValue(), part);
            }
        }
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        if (pattern == 0) {
            return "damage " + players.toString(event, debug) + " by " + amount.toString(event, debug) + " with cause " + damageCause.toString(event, debug);
        }
        return "damage " + bodyPart.toString(event, debug) + " of " + players.toString(event, debug) + " by " + amount.toString(event, debug) + " with cause " + damageCause.toString(event, debug);
    }
}
