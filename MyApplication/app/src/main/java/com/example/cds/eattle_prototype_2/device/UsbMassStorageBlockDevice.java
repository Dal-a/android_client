package com.example.cds.eattle_prototype_2.device;

import com.example.cds.eattle_prototype_2.scsi.Read10ScsiCommand;
import com.example.cds.eattle_prototype_2.scsi.ReadCapacity10ScsiCommand;
import com.example.cds.eattle_prototype_2.scsi.Write10ScsiCommand;

/**
 * Created by hyeonguk on 15. 2. 19..
 */
public class UsbMassStorageBlockDevice implements BlockDevice {

    final private UsbSerialDevice usbSerialDevice;
    final private long lastLogicalBlockAddress, blockLength;
    final private byte[] csw;

    private void readCsw() {
        usbSerialDevice.read(csw);
    }

    public UsbMassStorageBlockDevice(UsbSerialDevice usbSerialDevice) {
        this.usbSerialDevice = usbSerialDevice;
        csw = new byte[512];

        ReadCapacity10ScsiCommand command = new ReadCapacity10ScsiCommand();
        usbSerialDevice.write(command.generateCommand());
        byte[] readBuffer = new byte[512];
        usbSerialDevice.read(readBuffer);

        readCsw();

        lastLogicalBlockAddress = ((((int) readBuffer[0]) & 0xff) << 24) + ((((int) readBuffer[1]) & 0xff) << 16) + ((((int) readBuffer[2]) & 0xff) << 8) + (((int) readBuffer[3]) & 0xff);
        blockLength = (readBuffer[4] << 24) + (readBuffer[5] << 16) + (readBuffer[6] << 8) + readBuffer[7];
    }

    @Override
    public void readBlock(int lba, byte[] buffer) {
        lba += 13;
        Read10ScsiCommand command = new Read10ScsiCommand();
        command.setLba(lba);
        usbSerialDevice.write(command.generateCommand());
        usbSerialDevice.read(buffer);
        readCsw();
    }

    @Override
    public void writeBlock(int lba, byte[] buffer) {
        lba += 13;
        Write10ScsiCommand command = new Write10ScsiCommand();
        command.setLba(lba);
        usbSerialDevice.write(command.generateCommand());
        usbSerialDevice.write(buffer);
        readCsw();
    }

    @Override
    public long getLastLogicalBlockAddress() {
        return lastLogicalBlockAddress;
    }

    @Override
    public long getBlockLength() {
        return blockLength;
    }
}