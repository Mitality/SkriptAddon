package mitality.skriptAddon.elements.expressions;

import bodyhealth.api.BodyHealthAPI;
import bodyhealth.core.BodyPart;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;

@Name("Body Part Health")
@Description("The current health percentage (0-100) of a player's body part. Supports set, add (heals), and remove (damages).")
@Examples({
    "set {_hp} to body health of head of player",
    "set body health of head of player to 75",
    "add 20 to body health of left arm of player",
    "remove 15 from body health of right leg of player"
})
@Since("1.0.0")
public class ExprBodyPartHealth extends SimpleExpression<Number> {

    public static final String[] PATTERNS = {
        "[the] [body] health of %bodypart% of %player%"
    };

    public static void register(SyntaxRegistry registry) {
        registry.register(SyntaxRegistry.EXPRESSION, SyntaxInfo.Expression.builder(ExprBodyPartHealth.class, Number.class)
                .addPatterns(PATTERNS)
                .build());
    }

    private Expression<BodyPart> bodyPart;
    private Expression<Player> player;

    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        bodyPart = (Expression<BodyPart>) exprs[0];
        player = (Expression<Player>) exprs[1];
        return true;
    }

    @Override
    protected @Nullable Number[] get(Event event) {
        BodyPart part = bodyPart.getSingle(event);
        Player p = player.getSingle(event);
        if (part == null || p == null) return new Number[0];
        return new Number[]{ BodyHealthAPI.getInstance().getHealth(p, part) };
    }

    @Override
    public @Nullable Class<?>[] acceptChange(Changer.ChangeMode mode) {
        return switch (mode) {
            case SET, ADD, REMOVE -> new Class[]{ Number.class };
            default -> null;
        };
    }

    @Override
    public void change(Event event, Object @Nullable [] delta, Changer.ChangeMode mode) {
        BodyPart part = bodyPart.getSingle(event);
        Player p = player.getSingle(event);
        if (part == null || p == null || delta == null || delta[0] == null) return;
        double value = ((Number) delta[0]).doubleValue();
        BodyHealthAPI api = BodyHealthAPI.getInstance();
        switch (mode) {
            case SET -> api.setHealth(p, part, value);
            case ADD -> api.healPlayer(p, part, (int) value);
            case REMOVE -> api.damagePlayerDirectly(p, value, part);
        }
    }

    @Override
    public boolean isSingle() { return true; }

    @Override
    public Class<? extends Number> getReturnType() { return Number.class; }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "body health of " + bodyPart.toString(event, debug) + " of " + player.toString(event, debug);
    }
}
