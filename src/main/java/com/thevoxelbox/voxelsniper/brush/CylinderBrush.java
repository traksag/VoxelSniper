package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.brush.perform.PerformBrush;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;

/**
 * @author Kavutop
 */
public class CylinderBrush extends PerformBrush
{
    private static int timesUsed = 0;
    private double trueCircle = 0;

    /**
     *
     */
    public CylinderBrush()
    {
        this.setName("Cylinder");
    }

    private void cylinder(final SnipeData v, Block targetBlock)
    {
        final int _bSize = v.getBrushSize();
        int _starringY = targetBlock.getY() + v.getcCen();
        int _endTopY = targetBlock.getY() + v.getVoxelHeight() + v.getcCen();

        if (_endTopY < _starringY)
        {
            _endTopY = _starringY;
        }
        if (_starringY < 0)
        {
            _starringY = 0;
            v.sendMessage(ChatColor.DARK_PURPLE + "Warning: off-world start position.");
        }
        else if (_starringY > this.getWorld().getMaxHeight() - 1)
        {
            _starringY = this.getWorld().getMaxHeight() - 1;
            v.sendMessage(ChatColor.DARK_PURPLE + "Warning: off-world start position.");
        }
        if (_endTopY < 0)
        {
            _endTopY = 0;
            v.sendMessage(ChatColor.DARK_PURPLE + "Warning: off-world end position.");
        }
        else if (_endTopY > this.getWorld().getMaxHeight() - 1)
        {
            _endTopY = this.getWorld().getMaxHeight() - 1;
            v.sendMessage(ChatColor.DARK_PURPLE + "Warning: off-world end position.");
        }

        final double _bPow = Math.pow(_bSize + this.trueCircle, 2);

        for (int y = _endTopY; y >= _starringY; y--)
        {
            for (int x = _bSize; x >= 0; x--)
            {
                final double _xPow = Math.pow(x, 2);

                for (int z = _bSize; z >= 0; z--)
                {
                    if ((_xPow + Math.pow(z, 2)) <= _bPow)
                    {
                        this.current.perform(this.clampY(targetBlock.getX() + x, y, targetBlock.getZ() + z));
                        this.current.perform(this.clampY(targetBlock.getX() + x, y, targetBlock.getZ() - z));
                        this.current.perform(this.clampY(targetBlock.getX() - x, y, targetBlock.getZ() + z));
                        this.current.perform(this.clampY(targetBlock.getX() - x, y, targetBlock.getZ() - z));
                    }
                }
            }
        }
        v.storeUndo(this.current.getUndo());
    }

    @Override
    protected final void arrow(final SnipeData v)
    {
        this.cylinder(v, this.getTargetBlock());
    }

    @Override
    protected final void powder(final SnipeData v)
    {
        this.cylinder(v, this.getLastBlock());
    }

    @Override
    public final void info(final Message vm)
    {
        vm.brushName(this.getName());
        vm.size();
        vm.height();
        vm.center();
    }

    @Override
    public final void parameters(final String[] par, final SnipeData v)
    {
        for (int _i = 1; _i < par.length; _i++)
        {
            final String _param = par[_i];

            if (_param.equalsIgnoreCase("info"))
            {
                v.sendMessage(ChatColor.GOLD + "Cylinder Brush Parameters:");
                v.sendMessage(ChatColor.AQUA + "/b c h[number] -- set the cylinder v.voxelHeight.  Default is 1.");
                v.sendMessage(ChatColor.DARK_AQUA + "/b c true -- will use a true circle algorithm instead of the skinnier version with classic sniper nubs. /b b false will switch back. (false is default)");
                v.sendMessage(ChatColor.DARK_BLUE + "/b c c[number] -- set the origin of the cylinder compared to the target block. Positive numbers will move the cylinder upward, negative will move it downward.");
                return;
            }
            if (_param.startsWith("true"))
            {
                this.trueCircle = 0.5;
                v.sendMessage(ChatColor.AQUA + "True circle mode ON.");
                continue;
            }
            else if (_param.startsWith("false"))
            {
                this.trueCircle = 0;
                v.sendMessage(ChatColor.AQUA + "True circle mode OFF.");
                continue;
            }
            else if (_param.startsWith("h"))
            {
                v.setVoxelHeight((int) Double.parseDouble(_param.replace("h", "")));
                v.sendMessage(ChatColor.AQUA + "Cylinder v.voxelHeight set to: " + v.getVoxelHeight());
                continue;
            }
            else if (_param.startsWith("c"))
            {
                v.setcCen((int) Double.parseDouble(_param.replace("c", "")));
                v.sendMessage(ChatColor.AQUA + "Cylinder origin set to: " + v.getcCen());
                continue;
            }
            else
            {
                v.sendMessage(ChatColor.RED + "Invalid brush parameters! use the info parameter to display parameter info.");
            }
        }
    }

    @Override
    public final int getTimesUsed()
    {
        return CylinderBrush.timesUsed;
    }

    @Override
    public final void setTimesUsed(final int tUsed)
    {
        CylinderBrush.timesUsed = tUsed;
    }
}
