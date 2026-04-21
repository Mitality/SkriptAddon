package mitality.skriptAddon.elements.events;

import bodyhealth.api.events.BodyPartStateChangeEvent;
import bodyhealth.core.BodyPart;
import bodyhealth.core.BodyPartState;
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

@Name("Body Part State Change")
@Description({
    "Called whenever any body part of any player changes its BodyPartState.",
    "Note: old and new states may be null when a player joins, leaves, or the system is reloaded.",
    "You can access event-player, event-bodypart, event-bodypartstate (new state), and past event-bodypartstate (old state).",
    "You can optionally filter by body part in the event declaration."
})
@Examples({
    "on body part state change:",
    "    if event-bodypartstate is broken:",
    "        send \"Your %event-bodypart% is broken!\" to event-player",
    "",
    "on body part state change of left leg:",
    "    if event-bodypartstate is broken:",
    "        send \"Your left leg broke!\" to event-player"
})
@Since("1.0.0")
public class EvtBodyPartStateChange extends SkriptEvent {

    public static void register(SyntaxRegistry registry, EventValueRegistry eventRegistry) {
        registry.register(BukkitSyntaxInfos.Event.KEY,
                BukkitSyntaxInfos.Event.builder(EvtBodyPartStateChange.class, "Body Part State Change")
                        .addPatterns("[on] body part state change [of %-bodypart%]")
                        .addEvents(List.of(BodyPartStateChangeEvent.class))
                        .build());

        eventRegistry.register(EventValue.builder(BodyPartStateChangeEvent.class, Player.class)
                .getter(BodyPartStateChangeEvent::getPlayer)
                .time(EventValue.Time.NOW)
                .build());

        eventRegistry.register(EventValue.builder(BodyPartStateChangeEvent.class, BodyPart.class)
                .getter(BodyPartStateChangeEvent::getBodyPart)
                .time(EventValue.Time.NOW)
                .build());

        // New state (current/default time) — may be null on join/leave
        eventRegistry.register(EventValue.builder(BodyPartStateChangeEvent.class, BodyPartState.class)
                .getter(BodyPartStateChangeEvent::getNewState)
                .time(EventValue.Time.NOW)
                .build());

        // Old state (past time) — may be null on join/leave
        eventRegistry.register(EventValue.builder(BodyPartStateChangeEvent.class, BodyPartState.class)
                .getter(BodyPartStateChangeEvent::getOldState)
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
        if (!(event instanceof BodyPartStateChangeEvent e)) return false;
        if (bodyPartFilter == null) return true;
        return bodyPartFilter.check(event, part -> part == e.getBodyPart());
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        if (bodyPartFilter != null) {
            return "body part state change of " + bodyPartFilter.toString(event, debug);
        }
        return "body part state change";
    }
}
