package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Undo;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;

/**
 * THIS BRUSH SHOULD NOT USE PERFORMERS.
 * http://www.voxelwiki.com/minecraft/Voxelsniper#Shell_Brushes
 *
 * @author Voxel
 */
public class ShellBallBrush extends Brush
{
    private static int timesUsed = 0;

    /**
     *
     */
    public ShellBallBrush()
    {
        this.setName("Shell Ball");
    }

    // parameters isn't an abstract method, gilt. You can just leave it out if there are none.
    private void bShell(final SnipeData v, Block targetBlock)
    {
        final int _brushSize = v.getBrushSize();
        final int _twoBrushSize = 2 * _brushSize;
        final int _voxelMaterialId = v.getVoxelId();
        final int _voxelReplaceMaterialId = v.getReplaceId();
        final int[][][] _oldmats = new int[2 * (_brushSize + 1) + 1][2 * (_brushSize + 1) + 1][2 * (_brushSize + 1) + 1]; // Array that holds the original materials plus a buffer
        final int[][][] _newmats = new int[_twoBrushSize + 1][_twoBrushSize + 1][_twoBrushSize + 1]; // Array that holds the hollowed materials

        int blockPositionX = targetBlock.getX();
        int blockPositionY = targetBlock.getY();
        int blockPositionZ = targetBlock.getZ();
        // Log current materials into oldmats
        for (int _x = 0; _x <= 2 * (_brushSize + 1); _x++)
        {
            for (int _y = 0; _y <= 2 * (_brushSize + 1); _y++)
            {
                for (int _z = 0; _z <= 2 * (_brushSize + 1); _z++)
                {
                    _oldmats[_x][_y][_z] = this.getBlockIdAt(blockPositionX - _brushSize - 1 + _x, blockPositionY - _brushSize - 1 + _y, blockPositionZ - _brushSize - 1 + _z);
                }
            }
        }

        // Log current materials into newmats
        for (int _x = 0; _x <= _twoBrushSize; _x++)
        {
            for (int _y = 0; _y <= _twoBrushSize; _y++)
            {
                for (int _z = 0; _z <= _twoBrushSize; _z++)
                {
                    _newmats[_x][_y][_z] = _oldmats[_x + 1][_y + 1][_z + 1];
                }
            }
        }

        int _temp;

        // Hollow Brush Area
        for (int _x = 0; _x <= _twoBrushSize; _x++)
        {
            for (int _y = 0; _y <= _twoBrushSize; _y++)
            {
                for (int _z = 0; _z <= _twoBrushSize; _z++)
                {
                    _temp = 0;

                    if (_oldmats[_x + 1 + 1][_y + 1][_z + 1] == _voxelReplaceMaterialId)
                    {
                        _temp++;
                    }
                    if (_oldmats[_x + 1 - 1][_y + 1][_z + 1] == _voxelReplaceMaterialId)
                    {
                        _temp++;
                    }
                    if (_oldmats[_x + 1][_y + 1 + 1][_z + 1] == _voxelReplaceMaterialId)
                    {
                        _temp++;
                    }
                    if (_oldmats[_x + 1][_y + 1 - 1][_z + 1] == _voxelReplaceMaterialId)
                    {
                        _temp++;
                    }
                    if (_oldmats[_x + 1][_y + 1][_z + 1 + 1] == _voxelReplaceMaterialId)
                    {
                        _temp++;
                    }
                    if (_oldmats[_x + 1][_y + 1][_z + 1 - 1] == _voxelReplaceMaterialId)
                    {
                        _temp++;
                    }

                    if (_temp == 0)
                    {
                        _newmats[_x][_y][_z] = _voxelMaterialId;
                    }
                }
            }
        }

        // Make the changes
        final Undo _undo = new Undo(targetBlock.getWorld().getName());
        final double _rPow = Math.pow(_brushSize + 0.5, 2);

        for (int _x = _twoBrushSize; _x >= 0; _x--)
        {
            final double _xPow = Math.pow(_x - _brushSize, 2);

            for (int _y = 0; _y <= 2 * _brushSize; _y++)
            {
                final double _yPow = Math.pow(_y - _brushSize, 2);

                for (int _z = 2 * _brushSize; _z >= 0; _z--)
                {
                    if (_xPow + _yPow + Math.pow(_z - _brushSize, 2) <= _rPow)
                    {
                        if (this.getBlockIdAt(blockPositionX - _brushSize + _x, blockPositionY - _brushSize + _y, blockPositionZ - _brushSize + _z) != _newmats[_x][_y][_z])
                        {
                            _undo.put(this.clampY(blockPositionX - _brushSize + _x, blockPositionY - _brushSize + _y, blockPositionZ - _brushSize + _z));
                        }
                        this.setBlockIdAt(blockPositionZ - _brushSize + _z, blockPositionX - _brushSize + _x, blockPositionY - _brushSize + _y, _newmats[_x][_y][_z]);
                    }
                }
            }
        }
        v.storeUndo(_undo);

        v.owner().getPlayer().sendMessage(ChatColor.AQUA + "Shell complete."); // This is needed because most uses of this brush will not be sible to the
        // sniper.
    }

    @Override
    protected final void arrow(final SnipeData v)
    {
        this.bShell(v, this.getTargetBlock());
    }

    @Override
    protected final void powder(final SnipeData v)
    {
        this.bShell(v, this.getLastBlock());
    }

    @Override
    public final void info(final Message vm)
    {
        vm.brushName(this.getName());
        vm.size();
        vm.voxel();
        vm.replace();
    }

    @Override
    public final int getTimesUsed()
    {
        return ShellBallBrush.timesUsed;
    }

    @Override
    public final void setTimesUsed(final int tUsed)
    {
        ShellBallBrush.timesUsed = tUsed;
    }
}
