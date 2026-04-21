package mitality.skriptAddon.elements.events;

import bodyhealth.api.events.BodyPartHealthChangeEvent;
import bodyhealth.core.BodyPart;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.SkriptParser;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.bukkit.lang.eventvalue.EventValue;
import org.skriptlang.skript.bukkit.lang.eventvalue.EventValueRegistry;
import org.skriptlang.skript.bukkit.registration.BukkitSyntaxInfos;
import org.skriptlang.skript.registration.SyntaxRegistry;

import java.util.List;

@Name("Body Part Health Change")
@Description({
    "Called whenever the health value (in percent) of any body part of any player changes.",
    "The event is cancellable. You can access event-player, event-bodypart, event-number (new health), and past event-number (old health).",
    "You can optionally filter by body part in the event declaration."
})
@Examples({
    "on body part health change:",
    "    if event-bodypart is head:",
    "        send \"Your head took damage!\" to event-player",
    "",
    "on body part health change of head:",
    "    cancel event"
})
@Since("1.0.0")
public class EvtBodyPartHealthChange extends SkriptEvent {

    public static void register(SyntaxRegistry registry, EventValueRegistry eventRegistry) {
        registry.register(BukkitSyntaxInfos.Event.KEY,
                BukkitSyntaxInfos.Event.builder(EvtBodyPartHealthChange.class, "Body Part Health Change")
                        .addPatterns("[on] body part health change [of %-bodypart%]")
                        .addEvents(List.of(BodyPartHealthChangeEvent.class))
                        .build());

        eventRegistry.register(EventValue.builder(BodyPartHealthChangeEvent.class, Player.class)
                .getter(BodyPartHealthChangeEvent::getPlayer)
                .time(EventValue.Time.NOW)
                .build());

        eventRegistry.register(EventValue.builder(BodyPartHealthChangeEvent.class, BodyPart.class)
                .getter(BodyPartHealthChangeEvent::getBodyPart)
                .time(EventValue.Time.NOW)
                .build());

        // New health (current/default time)
        eventRegistry.register(EventValue.builder(BodyPartHealthChangeEvent.class, Number.class)
                .getter(BodyPartHealthChangeEvent::getNewHealth)
                .time(EventValue.Time.NOW)
                .build());

        // Old health (past time)
        eventRegistry.register(EventValue.builder(BodyPartHealthChangeEvent.class, Number.class)
                .getter(BodyPartHealthChangeEvent::getOldHealth)
                .time(EventValue.Time.PAST)
                .build());
    }

    private @Nullable Literal<BodyPart> bodyPartFilter;

    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Literal<?>[] args, int matchedPattern, SkriptParser.ParseResult parseResult) {
        bodyPartFilter = (Literal<BodyPart>) args[0];
        return true;
    }

    @Override
    public boolean check(Event event) {
        if (!(event instanceof BodyPartHealthChangeEvent e)) return false;
        if (bodyPartFilter == null) return true;
        return bodyPartFilter.check(event, part -> part == e.getBodyPart());
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        if (bodyPartFilter != null) {
            return "body part health change of " + bodyPartFilter.toString(event, debug);
        }
        return "body part health change";
    }
}
