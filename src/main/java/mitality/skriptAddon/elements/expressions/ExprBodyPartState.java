package mitality.skriptAddon.elements.expressions;

import bodyhealth.api.BodyHealthAPI;
import bodyhealth.core.BodyPart;
import bodyhealth.core.BodyPartState;
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

@Name("Body Part State")
@Description("The current state of a player's body part: full, nearly full, intermediate, damaged, or broken.")
@Examples({
    "set {_state} to body part state of head of player",
    "if body part state of left leg of player is broken:"
})
@Since("1.0.0")
public class ExprBodyPartState extends SimpleExpression<BodyPartState> {

    public static final String[] PATTERNS = {
        "[the] [body part] state of %bodypart% of %player%"
    };

    public static void register(SyntaxRegistry registry) {
        registry.register(SyntaxRegistry.EXPRESSION, SyntaxInfo.Expression.builder(ExprBodyPartState.class, BodyPartState.class)
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
    protected @Nullable BodyPartState[] get(Event event) {
        BodyPart part = bodyPart.getSingle(event);
        Player p = player.getSingle(event);
        if (part == null || p == null) return new BodyPartState[0];
        return new BodyPartState[]{ BodyHealthAPI.getInstance().getBodyHealthState(p, part) };
    }

    @Override
    public boolean isSingle() { return true; }

    @Override
    public Class<? extends BodyPartState> getReturnType() { return BodyPartState.class; }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "body part state of " + bodyPart.toString(event, debug) + " of " + player.toString(event, debug);
    }
}
