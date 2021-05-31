package hunternif.voxarch.mc.gdmc

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import hunternif.voxarch.mc.toXZ
import net.minecraft.command.CommandSource
import net.minecraft.command.Commands
import net.minecraft.command.arguments.BlockPosArgument.blockPos
import net.minecraft.command.arguments.BlockPosArgument.getLoadedBlockPos
import net.minecraft.util.Util
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.StringTextComponent
import net.minecraft.world.server.ServerWorld

private const val COMMAND_NAME = "buildsettlement"

fun register(dispatcher: CommandDispatcher<CommandSource>) {
    dispatcher.register(
        Commands.literal(COMMAND_NAME)
            .then(Commands.argument("from", blockPos())
                .then(Commands.argument("to", blockPos())
                    .executes { context -> perform(
                        context,
                        getLoadedBlockPos(context, "from"),
                        getLoadedBlockPos(context, "to")
                    )}
                )
            )
    )
}

private fun perform(
    context: CommandContext<CommandSource>,
    from: BlockPos,
    to: BlockPos
): Int {
    val world: ServerWorld = context.source.server.overworld()
    val source = context.source
    source.chatMessage("Will build a settlement somewhere from " +
        "${from.prettyXz()} to ${to.prettyXz()}")
    source.chatMessage("Building...")
    buildSettlement(world, from.toXZ(), to.toXZ())
    source.chatMessage("Finished building")
    return 1
}

private fun BlockPos.prettyXz() = "($x, $z)"

private fun CommandSource.chatMessage(msg: String) {
    this.entity?.sendMessage(StringTextComponent(msg), Util.NIL_UUID)
}