package com.kyriakosalexandrou.bluetoothtoolkit.bt.ble;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.UUID;

/**
 * Represents a remote BLE device.
 * <p/>
 * When a class extends this class it immediately has access to all the BLE
 * methods needed to initialise a BLE remote device connection and
 * communications with the connected remote device.
 * <p/>
 * Provides all the general/shared methods for BLE device types, each device
 * type (htm, heart rate, ect.) will have their own manager but extend
 * the BleBaseActivity. This removes a lot of duplicate code so class' are
 * cleaner and more concise.
 * <p/>
 * <p/>
 * Use this class to do any BLE operations. <br>
 * For example you can use: <br>
 * - connect(BluetoothDevice device, boolean autoConnect) to connect to a remote
 * BLE device <br>
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public abstract class BleBaseDeviceManager extends BluetoothGattCallback {

    private final static String TAG = "BleBaseDeviceManager";

    protected Context mContext;
    protected BluetoothDevice mBluetoothDevice = null;
    protected BluetoothManager mBluetoothManager;

    protected static final Integer RSSI_UPDATE_INTERVAL = 2000;
    protected BluetoothGatt mBluetoothGatt = null;
    protected final Queue<BluetoothGattCharacteristic> mCharQueue = new LinkedList<>();
    private BleBaseDeviceManagerUiCallback mBleBaseDeviceManagerUiCallback;

    private int mRSSIValue;
    private int mBatteryValue;

    private boolean mIsReceiverRegistered = false;
    private boolean mRssiTimerEnabled = false;
    private final Handler mRssiTimerHandler = new Handler();

    /**
     * initialise an object that represents a remote BLE device. Use
     * {@link #connect(BluetoothDevice, boolean)} to connect to the remote BLE
     * device
     *
     * @param context the application context
     */
    public BleBaseDeviceManager(Context context,
                                BleBaseDeviceManagerUiCallback bleBaseDeviceManagerUiCallback) {
        if (context == null || bleBaseDeviceManagerUiCallback == null)
            throw new NullPointerException(
                    "Context or BleBaseDeviceManagerUiCallback object passed is null!");

        mContext = context;
        mBluetoothManager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
        mBleBaseDeviceManagerUiCallback = bleBaseDeviceManagerUiCallback;
        mBleBaseDeviceManagerUiCallback.onUiDeviceManagerInitialised(this);
    }

    public BluetoothDevice getBluetoothDevice() {
        return mBluetoothDevice;
    }

    public BluetoothGatt getBluetoothGatt() {
        return mBluetoothGatt;
    }

    public int getBatteryValue() {
        return mBatteryValue;
    }

    public int getRssiValue() {
        return mRSSIValue;
    }

    /**
     * Connects to the given BluetoothDevice object
     *
     * @param device      the current BluetoothDevice to connect to
     * @param autoConnect Whether to directly connect to the remote device (false) or to
     *                    automatically connect as soon as the remote device becomes
     *                    available (true).
     */
    public void connect(BluetoothDevice device, boolean autoConnect) {
        mBluetoothDevice = device;
        mBluetoothGatt = mBluetoothDevice.connectGatt(mContext, autoConnect, this);
    }

    /**
     * Get the current connection state of the profile to the remote device.
     * <p/>
     * <p/>
     * Requires {@link android.Manifest.permission#BLUETOOTH} permission.
     *
     * @return State of the profile connection. One of
     * {@link BluetoothProfile#STATE_CONNECTED},
     * {@link BluetoothProfile#STATE_CONNECTING},
     * {@link BluetoothProfile#STATE_DISCONNECTED},
     * {@link BluetoothProfile#STATE_DISCONNECTING}
     */
    public int getConnectionState() {
        return mBluetoothManager.getConnectionState(mBluetoothDevice,
                BluetoothProfile.GATT);
    }

    /**
     * disconnects with the remote device
     */
    public void disconnect() {
        if (mBluetoothGatt != null) {
            mBluetoothGatt.disconnect();
        }
    }

    /**
     * Reads the RSSI value periodically with a time interval passed as a
     * parameter.
     *
     * @param shouldRepeat pass true if need to get the RSSI value periodically,
     *                     otherwise false
     * @param timeInterval the time in milliseconds to retrieve the RSSI value
     *                     periodically
     */
    protected void readRssiPeriodicaly(final boolean shouldRepeat,
                                       final int timeInterval) {
        mRssiTimerEnabled = shouldRepeat;

        mRssiTimerHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mBluetoothGatt != null
                        && getConnectionState() != BluetoothProfile.STATE_CONNECTED) {
                    mRssiTimerEnabled = false;
                    return;
                } else if (mBluetoothGatt != null) {
                    // request RSSI value
                    mBluetoothGatt.readRemoteRssi(); // the onReadRemoteRssi callback gets called
                    // and call it once more in the future
                    readRssiPeriodicaly(mRssiTimerEnabled, timeInterval);
                }
            }
        }, timeInterval);
    }

    /**
     * enables notifications or indications of the given characteristic based on the action given
     *
     * @param DescriptorAction the action the method should fire. Can be one of
     *                         {@link DescriptorAction#BOTH} or
     *                         {@link DescriptorAction#NOTIFICATION} or
     *                         {@link DescriptorAction#INDICATION}
     * @param characteristic
     * @param shouldEnable     true to enable notification/indication, or false to disable them.
     * @return true if action was fired successful
     */
    protected boolean setCharacteristicNotificationOrIndication(
            DescriptorAction DescriptorAction,
            BluetoothGattCharacteristic characteristic, boolean shouldEnable) {
        if (mBluetoothGatt == null)
            throw new NullPointerException("mBluetoothGatt object is null!");

        boolean isSettingNotificationLocallySuccess = mBluetoothGatt
                .setCharacteristicNotification(characteristic, shouldEnable);

        if (isSettingNotificationLocallySuccess) {
            boolean descrInitiatedSuccessfully = false;

            switch (DescriptorAction) {
                case BOTH:
                    descrInitiatedSuccessfully =
                            writeDescriptorForNotificationOrIndication(characteristic, shouldEnable);
                    break;
                case NOTIFICATION:
                    descrInitiatedSuccessfully =
                            writeDescriptorForNotification(characteristic, shouldEnable);
                    break;
                case INDICATION:
                    descrInitiatedSuccessfully =
                            writeDescriptorForIndication(characteristic, shouldEnable);
                    break;
            }
            return descrInitiatedSuccessfully;
        } else {
            return false;
        }
    }

    public enum DescriptorAction {
        BOTH, NOTIFICATION, INDICATION
    }

    /**
     * Finds all the services and characteristics that the remote BLE device
     * has. The
     * {@link BleBaseDeviceManager#onServiceFound(BluetoothGattService)} method
     * is called whenever a service is found and the
     * {@link BleBaseDeviceManager#onCharFound(BluetoothGattCharacteristic)}
     * method is called whenever a characteristic is found. Finally the
     * {@link BleBaseDeviceManager#onCharsFoundCompleted()}
     */
    protected void findServicesAndCharacteristics() {
        if (mBluetoothGatt == null) {
            Log.w(TAG, "mBluetoothGatt is null");
            return;
        }

        List<BluetoothGattService> services = mBluetoothGatt.getServices();

        for (int i = 0; i < services.size(); i++) {
            onServiceFound(services.get(i));

            List<BluetoothGattCharacteristic> characteristics = services.get(i)
                    .getCharacteristics();

            for (int j = 0; j < characteristics.size(); j++) {
                onCharFound(characteristics.get(j));
            }
        }
        onCharsFoundCompleted();
    }

    /**
     * called once the {@link BleBaseDeviceManager#findServicesAndCharacteristics()}
     * method is completed
     */
    protected void onCharsFoundCompleted() {
    }

    /**
     * adds the given characteristic to the characteristics queue. Call the
     * {@link BleBaseDeviceManager#executeCharacteristicsQueue()} method to
     * execute the characteristics operation. Currently only read and
     * notification/indication operations are supported.
     *
     * @param characteristic the characteristic to add to the queue
     */
    protected void addCharToQueue(BluetoothGattCharacteristic characteristic) {
        mCharQueue.add(characteristic);
    }

    /**
     * executes the queued characteristics and automatically identifies if they
     * need to be read or notified/indicated and executes that operation.
     * <p/>
     * The {@link BleBaseDeviceManager#onCharacteristicsQueueCompleted()} method
     * is called once all characteristics have been executed
     */
    protected void executeCharacteristicsQueue() {
        // based on char properties @
        // https://msdn.microsoft.com/en-us/library/windows.devices.bluetooth.genericattributeprofile.gattcharacteristicproperties.aspx

        if (mCharQueue.size() >= 1) {
            Log.i(TAG, "executeCharacteristicsQueue: "
                    + mCharQueue.element().getUuid());

            // if char is readable, read char
            if ((mCharQueue.element().getProperties() & BluetoothGattCharacteristic.PROPERTY_READ) == BluetoothGattCharacteristic.PROPERTY_READ) {
                mBluetoothGatt.readCharacteristic(mCharQueue.element());
            }
            // if char is notify || indicate, enable
            else if ((mCharQueue.element().getProperties() & BluetoothGattCharacteristic.PROPERTY_NOTIFY) == BluetoothGattCharacteristic.PROPERTY_NOTIFY
                    || (mCharQueue.element().getProperties() & BluetoothGattCharacteristic.PROPERTY_INDICATE) == BluetoothGattCharacteristic.PROPERTY_INDICATE) {
                setCharacteristicNotificationOrIndication(
                        DescriptorAction.BOTH,
                        mCharQueue.element(),
                        true);
            }
        } else {
            onCharacteristicsQueueCompleted();
        }
    }

    /**
     * called once the
     * {@link BleBaseDeviceManager#executeCharacteristicsQueue()} method is
     * completed (after doing all the characteristics operations)
     */
    protected void onCharacteristicsQueueCompleted() {
    }

    /**
     * write the Client Characteristic Configuration Descriptor (CCCD) for
     * enabling/disabling notifications/indications to a specific
     * characteristic.
     *
     * @param ch      the characteristic to write the CCCD descriptor to
     * @param enabled false to disable notifications/indications, true to enable
     *                them
     * @return boolean false if CCCD descriptor was not found or if the
     * operation was not initiated successful, otherwise returns true
     */
    private boolean writeDescriptorForNotificationOrIndication(
            BluetoothGattCharacteristic ch, boolean enabled) {
        BluetoothGattDescriptor descriptor = getCccdDescriptor(ch);

        if (descriptor == null)
            return false;

        int properties = ch.getProperties();

        if ((BluetoothGattCharacteristic.PROPERTY_NOTIFY & properties) != 0) {
            // set notifications, heart rate measurement etc
            byte[] val = enabled ? BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                    : BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE;
            boolean descrSuccess = descriptor.setValue(val);
            Log.i(TAG, "NOTIFY: " + descrSuccess);
        } else if ((BluetoothGattCharacteristic.PROPERTY_INDICATE & properties) != 0) {
            // set notifications, temperature measurement etc
            byte[] val = enabled ? BluetoothGattDescriptor.ENABLE_INDICATION_VALUE
                    : BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE;
            boolean descrSuccess = descriptor.setValue(val);
            Log.i(TAG, "INDICATE: " + descrSuccess);
        }

        boolean success = mBluetoothGatt.writeDescriptor(descriptor);

        Log.i(TAG, "writeDescriptor success: " + success);
        return success;
    }

    private BluetoothGattDescriptor getCccdDescriptor(BluetoothGattCharacteristic ch) {
        //see: https://developer.bluetooth.org/gatt/descriptors/Pages/DescriptorViewer.aspx?u=org.bluetooth.descriptor.gatt.client_characteristic_configuration.xml
        return ch.getDescriptor(
                BleUUIDs.Descriptor.CLIENT_CHARACTERISTIC_CONFIGURATION_DESCRIPTOR);
    }

    private boolean writeDescriptorForNotification(final BluetoothGattCharacteristic ch,
                                                   final boolean enabled) {
        BluetoothGattDescriptor descriptor = getCccdDescriptor(ch);
        if (descriptor == null)
            return false;

        int properties = ch.getProperties();

        if ((BluetoothGattCharacteristic.PROPERTY_NOTIFY & properties) != 0) {
            // set notifications, heart rate etc.
            byte[] val = enabled ? BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                    : BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE;
            boolean descrSuccess = descriptor.setValue(val);
            Log.i(TAG, "NOTIFY: " + descrSuccess);
        }

        boolean success = mBluetoothGatt.writeDescriptor(descriptor);

        Log.i(TAG, "writeDescriptorForNotification success: " + success);
        return success;
    }

    private boolean writeDescriptorForIndication(final BluetoothGattCharacteristic ch,
                                                 final boolean enabled) {

        BluetoothGattDescriptor descriptor = getCccdDescriptor(ch);
        if (descriptor == null)
            return false;

        int properties = ch.getProperties();

        if ((BluetoothGattCharacteristic.PROPERTY_INDICATE & properties) != 0) {
            // set indications, temperature measurement etc.
            byte[] val = enabled ? BluetoothGattDescriptor.ENABLE_INDICATION_VALUE
                    : BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE;
            boolean descrSuccess = descriptor.setValue(val);
            Log.i(TAG, "INDICATE: " + descrSuccess);
        }

        boolean success = mBluetoothGatt.writeDescriptor(descriptor);

        Log.i(TAG, "writeDescriptorForIndication success: " + success);
        return success;
    }

    private void registerReceiver() {
        if (!mIsReceiverRegistered) {
            mIsReceiverRegistered = true;
            // registering Bluetooth BroadcastReceiver
            IntentFilter filter = new IntentFilter(
                    BluetoothDevice.ACTION_BOND_STATE_CHANGED);
            mContext.registerReceiver(mReceiver, filter);
        }
    }

    /**
     * the receiver gets unregistered when we are not in the procedure of
     * bonding and if we are disconnected or if we are disconnecting
     */
    private void unregisterReceiver() {
        if (mBluetoothDevice.getBondState() != BluetoothDevice.BOND_BONDING
                && mIsReceiverRegistered) {
            // unregister the receiver or else it would run in the background
            if (mReceiver != null) {
                mContext.unregisterReceiver(mReceiver);
                mIsReceiverRegistered = false;
            }
        }
    }

    /**
     * manages when to close the gatt object.
     * <p/>
     * The gatt is only closed when we are disconnected and if we are not
     * bonding
     */
    private void manageGatt() {
        /*
         * the GATT client should always be closed once we are done entirely
		 * with the remote device.
		 * 
		 * Note: if the gatt is closed while bonding the app will crash
		 */
        if (getConnectionState() == BluetoothProfile.STATE_DISCONNECTED
                && mBluetoothDevice.getBondState() != BluetoothDevice.BOND_BONDING) {
            closeGatt();
        }
    }

    private void closeGatt() {
        if (mBluetoothGatt != null) {
            mBluetoothGatt.close();
            mBluetoothGatt = null;
        }
    }

    /**
     * callback indicating bonding (pairing) is in progress with the remote
     * device.
     */
    protected void onBonding() {
    }

    /**
     * callback Indicates the remote device got bonded (paired). A shared link
     * keys exists locally for the remote device, so communication can be
     * authenticated and encrypted. Being bonded (paired) with a remote device
     * does not necessarily mean the device is currently connected. It just
     * means that the pending procedure was completed at some earlier time, and
     * the link key is still stored locally, ready to use on the next
     * connection.
     */
    protected void onBonded() {
    }

    /**
     * callback indicating the remote device is not bonded (paired).
     */
    protected void notBonded() {
    }

    /**
     * Callback that gets called every time a characteristic is found
     *
     * @param characteristic the characteristic that was found
     */
    protected void onCharFound(final BluetoothGattCharacteristic characteristic) {
        if (BleUUIDs.Service.BATTERY.equals(characteristic.getService()
                .getUuid())) {
            if (BleUUIDs.Characteristic.BATTERY_LEVEL
                    .equals(characteristic.getUuid())) {
                addCharToQueue(characteristic);
            }
        }
    }

    /**
     * Callback that gets called whenever a service is found
     *
     * @param service
     */
    protected void onServiceFound(final BluetoothGattService service) {
    }

    public final BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
                final int state = intent
                        .getIntExtra(BluetoothDevice.EXTRA_BOND_STATE,
                                BluetoothDevice.ERROR);

                if (state == BluetoothDevice.BOND_BONDING) {
                    Log.i(TAG, "On Bonding...");
                    onBonding();

                } else if (state == BluetoothDevice.BOND_BONDED) {
                    Log.i(TAG, "On Bonded");

                    // if there are any chars in the queue finish their
                    // operation
                    executeCharacteristicsQueue();

                    // close the gatt if we are disconnected
                    manageGatt();

                    // unregister the receiver or else it would run in the
                    // background
                    unregisterReceiver();

                    onBonded();

                } else if (state == BluetoothDevice.BOND_NONE) {
                    Log.i(TAG, "Not Bonded");

                    notBonded();
                }
            }
        }
    };


    private void removeCharFromQueue() {
        if (!mCharQueue.isEmpty()) {
            Log.i(TAG, "Char removed from queue: "
                    + mCharQueue.element().getUuid());
            mCharQueue.remove();
        }
    }

    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status,
                                        int newState) {
        if (status == BluetoothGatt.GATT_SUCCESS) {
            switch (newState) {
                case BluetoothProfile.STATE_CONNECTING:
                    mBleBaseDeviceManagerUiCallback.onUiConnecting();
                    break;

                case BluetoothProfile.STATE_CONNECTED:
                    // so that we can get bonding operation events
                    registerReceiver();
                    mBluetoothGatt.discoverServices();
                    mBleBaseDeviceManagerUiCallback.onUiConnected();
                    break;

                case BluetoothProfile.STATE_DISCONNECTING:
                    mBleBaseDeviceManagerUiCallback.onUiDisconnecting();
                    break;

                case BluetoothProfile.STATE_DISCONNECTED:
                    manageGatt();
                    unregisterReceiver();
                    mBleBaseDeviceManagerUiCallback.onUiDisconnected(status);

                    break;
            }
        } else {
            manageGatt();
            unregisterReceiver();
            mBleBaseDeviceManagerUiCallback.onUiDisconnected(status);
        }
    }

    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
        findServicesAndCharacteristics();
    }

    @Override
    public void onCharacteristicRead(BluetoothGatt gatt,
                                     BluetoothGattCharacteristic characteristic, int status) {
        switch (status) {
            case BluetoothGatt.GATT_SUCCESS:
                UUID currentCharUUID = characteristic.getUuid();

                if (BleUUIDs.Characteristic.BATTERY_LEVEL
                        .equals(currentCharUUID)) {
                    mBatteryValue = characteristic.getIntValue(
                            BluetoothGattCharacteristic.FORMAT_UINT8, 0);
                    mBleBaseDeviceManagerUiCallback.onUiBatteryRead(mBatteryValue);
                }

                removeCharFromQueue();
                executeCharacteristicsQueue();

                break;

            case BluetoothGatt.GATT_INSUFFICIENT_AUTHENTICATION:
            case BluetoothGatt.GATT_INSUFFICIENT_ENCRYPTION:
                // bonds automatically
                Log.w(TAG,
                        "Failed to read char because of "
                                + "GATT_INSUFFICIENT_AUTHENTICATION" + " and "
                                + "GATT_INSUFFICIENT_ENCRYPTION: "
                                + characteristic.getUuid());
                break;

            default:
                Log.w(TAG, "Failed to read char: " + characteristic.getUuid());
                break;
        }
    }

    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt,
                                      BluetoothGattCharacteristic characteristic, int status) {
        switch (status) {
            case BluetoothGatt.GATT_SUCCESS:

                break;
            case BluetoothGatt.GATT_INSUFFICIENT_AUTHENTICATION:
            case BluetoothGatt.GATT_INSUFFICIENT_ENCRYPTION:
                // bonds automatically
                Log.w(TAG,
                        "Failed to write char because of "
                                + "GATT_INSUFFICIENT_AUTHENTICATION" + " and "
                                + "GATT_INSUFFICIENT_ENCRYPTION: "
                                + characteristic.getUuid());
                break;

            default:
                Log.w(TAG, "Failed to write char: " + characteristic.getUuid());
                break;
        }
    }

    @Override
    public void onDescriptorRead(BluetoothGatt gatt,
                                 BluetoothGattDescriptor descriptor, int status) {
        switch (status) {
            case BluetoothGatt.GATT_SUCCESS:
                removeCharFromQueue();
                executeCharacteristicsQueue();
                break;
            case BluetoothGatt.GATT_INSUFFICIENT_AUTHENTICATION:
            case BluetoothGatt.GATT_INSUFFICIENT_ENCRYPTION:
                // bonds automatically
                Log.w(TAG, "Failed to read descriptor because of "
                        + "GATT_INSUFFICIENT_AUTHENTICATION" + " and "
                        + "GATT_INSUFFICIENT_ENCRYPTION: " + descriptor.getUuid());
                break;

            default:
                Log.w(TAG, "Failed to read descriptor: " + descriptor.getUuid());
                break;
        }
    }

    @Override
    public void onDescriptorWrite(BluetoothGatt gatt,
                                  BluetoothGattDescriptor descriptor, int status) {
        switch (status) {
            case BluetoothGatt.GATT_SUCCESS:
                removeCharFromQueue();
                executeCharacteristicsQueue();
                break;
            case BluetoothGatt.GATT_INSUFFICIENT_AUTHENTICATION:
            case BluetoothGatt.GATT_INSUFFICIENT_ENCRYPTION:
                // bonds automatically
                Log.w(TAG, "Failed to write descriptor because of "
                        + "GATT_INSUFFICIENT_AUTHENTICATION" + " and "
                        + "GATT_INSUFFICIENT_ENCRYPTION: " + descriptor.getUuid());

                break;

            default:
                Log.w(TAG, "Failed to write descriptor: " + descriptor.getUuid());
                break;
        }
    }

    @Override
    public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
        switch (status) {
            case BluetoothGatt.GATT_SUCCESS:
                mRSSIValue = rssi;
                mBleBaseDeviceManagerUiCallback.onUiReadRemoteRssi(rssi);
                break;
            case BluetoothGatt.GATT_INSUFFICIENT_AUTHENTICATION:
            case BluetoothGatt.GATT_INSUFFICIENT_ENCRYPTION:
                // bonds automatically
                Log.w(TAG, "Failed to read RSSI because of "
                        + "GATT_INSUFFICIENT_AUTHENTICATION" + " and "
                        + "GATT_INSUFFICIENT_ENCRYPTION");
                break;

            default:
                Log.w(TAG, "Failed to read RSSI. Error status: " + status);
                break;
        }
    }

    @Override
    public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
        switch (status) {
            case BluetoothGatt.GATT_SUCCESS:
                removeCharFromQueue();
                break;
            case BluetoothGatt.GATT_INSUFFICIENT_AUTHENTICATION:
            case BluetoothGatt.GATT_INSUFFICIENT_ENCRYPTION:
                // bonds automatically
                Log.w(TAG, "Failed to do a reliable write because of "
                        + "GATT_INSUFFICIENT_AUTHENTICATION" + " and "
                        + "GATT_INSUFFICIENT_ENCRYPTION");
                break;

            default:
                Log.w(TAG, "Failed to do a reliable write. Status error: " + status);
                break;
        }
    }
}