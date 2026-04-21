package mitality.skriptAddon.elements.expressions;

import bodyhealth.api.BodyHealthAPI;
import bodyhealth.core.BodyPart;
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

@Name("Max Body Part Health")
@Description("The maximum health value for a player's body part, as calculated by BodyHealth.")
@Examples({
    "set {_max} to max body part health of head of player"
})
@Since("1.0.0")
public class ExprMaxPartHealth extends SimpleExpression<Number> {

    public static final String[] PATTERNS = {
        "[the] max[imum] [body part] health of %bodypart% of %player%"
    };

    public static void register(SyntaxRegistry registry) {
        registry.register(SyntaxRegistry.EXPRESSION, SyntaxInfo.Expression.builder(ExprMaxPartHealth.class, Number.class)
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
        return new Number[]{ BodyHealthAPI.getInstance().getMaxPartHealth(p, part) };
    }

    @Override
    public boolean isSingle() { return true; }

    @Override
    public Class<? extends Number> getReturnType() { return Number.class; }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "maximum body part health of " + bodyPart.toString(event, debug) + " of " + player.toString(event, debug);
    }
}
