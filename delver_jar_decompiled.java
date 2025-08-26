
    private static final Keyboard createKeyboardFromDevice(LinuxEventDevice device, Component[] components) throws IOException {
        LinuxKeyboard keyboard = new LinuxKeyboard(device, components, new Controller[0], device.getRumblers());
        return keyboard;
    }

    private static final Controller createJoystickFromDevice(LinuxEventDevice device, Component[] components, Controller.Type type) throws IOException {
        LinuxAbstractController joystick = new LinuxAbstractController(device, components, new Controller[0], device.getRumblers(), type);
        return joystick;
    }

    private static final Controller createControllerFromDevice(LinuxEventDevice device) throws IOException {
        List event_components = device.getComponents();
        Component[] components = LinuxEnvironmentPlugin.createComponents(event_components, device);
        Controller.Type type = device.getType();
        if (type == Controller.Type.MOUSE) {
            return LinuxEnvironmentPlugin.createMouseFromDevice(device, components);
        }
        if (type == Controller.Type.KEYBOARD) {
            return LinuxEnvironmentPlugin.createKeyboardFromDevice(device, components);
        }
        if (type == Controller.Type.STICK || type == Controller.Type.GAMEPAD) {
            return LinuxEnvironmentPlugin.createJoystickFromDevice(device, components, type);
        }
        return null;
    }

    private final Controller[] enumerateControllers() {
        ArrayList<LinuxCombinedController> controllers = new ArrayList<LinuxCombinedController>();
        ArrayList eventControllers = new ArrayList();
        ArrayList jsControllers = new ArrayList();
        this.enumerateEventControllers(eventControllers);
        this.enumerateJoystickControllers(jsControllers);
        block0: for (int i = 0; i < eventControllers.size(); ++i) {
            for (int j = 0; j < jsControllers.size(); ++j) {
                Component[] jsComponents;
                Component[] evComponents;
                Controller evController = (Controller)eventControllers.get(i);
                Controller jsController = (Controller)jsControllers.get(j);
                if (!evController.getName().equals(jsController.getName()) || (evComponents = evController.getComponents()).length != (jsComponents = jsController.getComponents()).length) continue;
                boolean foundADifference = false;
                for (int k = 0; k < evComponents.length; ++k) {
                    if (evComponents[k].getIdentifier() == jsComponents[k].getIdentifier()) continue;
                    foundADifference = true;
                }
                if (foundADifference) continue;
                controllers.add(new LinuxCombinedController((LinuxAbstractController)eventControllers.remove(i), (LinuxJoystickAbstractController)jsControllers.remove(j)));
                --i;
                --j;
                continue block0;
            }
        }
        controllers.addAll(eventControllers);
        controllers.addAll(jsControllers);
        Controller[] controllers_array = new Controller[controllers.size()];
        controllers.toArray(controllers_array);
        return controllers_array;
    }

    private static final Component.Identifier.Button getButtonIdentifier(int index) {
        switch (index) {
            case 0: {
                return Component.Identifier.Button._0;
            }
            case 1: {
                return Component.Identifier.Button._1;
            }
            case 2: {
                return Component.Identifier.Button._2;
            }
            case 3: {
                return Component.Identifier.Button._3;
            }
            case 4: {
                return Component.Identifier.Button._4;
            }
            case 5: {
                return Component.Identifier.Button._5;
            }
            case 6: {
                return Component.Identifier.Button._6;
            }
            case 7: {
                return Component.Identifier.Button._7;
            }
            case 8: {
                return Component.Identifier.Button._8;
            }
            case 9: {
                return Component.Identifier.Button._9;
            }
            case 10: {
                return Component.Identifier.Button._10;
            }
            case 11: {
                return Component.Identifier.Button._11;
            }
            case 12: {
                return Component.Identifier.Button._12;
            }
            case 13: {
                return Component.Identifier.Button._13;
            }
            case 14: {
                return Component.Identifier.Button._14;
            }
            case 15: {
                return Component.Identifier.Button._15;
            }
            case 16: {
                return Component.Identifier.Button._16;
            }
            case 17: {
                return Component.Identifier.Button._17;
            }
            case 18: {
                return Component.Identifier.Button._18;
            }
            case 19: {
                return Component.Identifier.Button._19;
            }
            case 20: {
                return Component.Identifier.Button._20;
            }
            case 21: {
                return Component.Identifier.Button._21;
            }
            case 22: {
                return Component.Identifier.Button._22;
            }
            case 23: {
                return Component.Identifier.Button._23;
            }
            case 24: {
                return Component.Identifier.Button._24;
            }
            case 25: {
                return Component.Identifier.Button._25;
            }
            case 26: {
                return Component.Identifier.Button._26;
            }
            case 27: {
                return Component.Identifier.Button._27;
            }
            case 28: {
                return Component.Identifier.Button._28;
            }
            case 29: {
                return Component.Identifier.Button._29;
            }
            case 30: {
                return Component.Identifier.Button._30;
            }
            case 31: {
                return Component.Identifier.Button._31;
            }
        }
        return null;
    }

    private static final Controller createJoystickFromJoystickDevice(LinuxJoystickDevice device) {
        int i;
        ArrayList<AbstractComponent> components = new ArrayList<AbstractComponent>();
        byte[] axisMap = device.getAxisMap();
        char[] buttonMap = device.getButtonMap();
        LinuxJoystickAxis[] hatBits = new LinuxJoystickAxis[6];
        for (i = 0; i < device.getNumButtons(); ++i) {
            Component.Identifier button_id = LinuxNativeTypesMap.getButtonID(buttonMap[i]);
            if (button_id == null) continue;
            LinuxJoystickButton button = new LinuxJoystickButton(button_id);
            device.registerButton(i, button);
            components.add(button);
        }
        for (i = 0; i < device.getNumAxes(); ++i) {
            Component.Identifier.Axis axis_id = (Component.Identifier.Axis)LinuxNativeTypesMap.getAbsAxisID(axisMap[i]);
            LinuxJoystickAxis axis = new LinuxJoystickAxis(axis_id);
            device.registerAxis(i, axis);
            if (axisMap[i] == 16) {
                hatBits[0] = axis;
                continue;
            }
            if (axisMap[i] == 17) {
                hatBits[1] = axis;
                axis = new LinuxJoystickPOV(Component.Identifier.Axis.POV, hatBits[0], hatBits[1]);
                device.registerPOV((LinuxJoystickPOV)axis);
                components.add(axis);
                continue;
            }
            if (axisMap[i] == 18) {
                hatBits[2] = axis;
                continue;
            }
            if (axisMap[i] == 19) {
                hatBits[3] = axis;
                axis = new LinuxJoystickPOV(Component.Identifier.Axis.POV, hatBits[2], hatBits[3]);
                device.registerPOV((LinuxJoystickPOV)axis);
                components.add(axis);
                continue;
            }
            if (axisMap[i] == 20) {
                hatBits[4] = axis;
                continue;
            }
            if (axisMap[i] == 21) {
                hatBits[5] = axis;
                axis = new LinuxJoystickPOV(Component.Identifier.Axis.POV, hatBits[4], hatBits[5]);
                device.registerPOV((LinuxJoystickPOV)axis);
                components.add(axis);
                continue;
            }
            components.add(axis);
        }
        return new LinuxJoystickAbstractController(device, components.toArray(new Component[0]), new Controller[0], new Rumbler[0]);
    }

    private final void enumerateJoystickControllers(List controllers) {
        File[] joystick_device_files = LinuxEnvironmentPlugin.enumerateJoystickDeviceFiles("/dev/input");
        if ((joystick_device_files == null || joystick_device_files.length == 0) && (joystick_device_files = LinuxEnvironmentPlugin.enumerateJoystickDeviceFiles("/dev")) == null) {
            return;
        }
        for (int i = 0; i < joystick_device_files.length; ++i) {
            File event_file = joystick_device_files[i];
            try {
                String path = LinuxEnvironmentPlugin.getAbsolutePathPrivileged(event_file);
                LinuxJoystickDevice device = new LinuxJoystickDevice(path);
                Controller controller = LinuxEnvironmentPlugin.createJoystickFromJoystickDevice(device);
                if (controller != null) {
                    controllers.add(controller);
                    this.devices.add(device);
                    continue;
                }
                device.close();
                continue;
            }
            catch (IOException e) {
                LinuxEnvironmentPlugin.logln("Failed to open device (" + event_file + "): " + e.getMessage());
            }
        }
    }

    private static final File[] enumerateJoystickDeviceFiles(String dev_path) {
        File dev = new File(dev_path);
        return LinuxEnvironmentPlugin.listFilesPrivileged(dev, new FilenameFilter(){

            public final boolean accept(File dir, String name) {
                return name.startsWith("js");
            }
        });
    }

    private static String getAbsolutePathPrivileged(final File file) {
        return (String)AccessController.doPrivileged(new PrivilegedAction(){

            public Object run() {
                return file.getAbsolutePath();
            }
        });
    }

    private static File[] listFilesPrivileged(final File dir, final FilenameFilter filter) {
        return (File[])AccessController.doPrivileged(new PrivilegedAction(){

            public Object run() {
                File[] files = dir.listFiles(filter);
                Arrays.sort(files, new Comparator(){

                    public int compare(Object f1, Object f2) {
                        return ((File)f1).getName().compareTo(((File)f2).getName());
                    }
                });
                return files;
            }
        });
    }

    private final void enumerateEventControllers(List controllers) {
        File dev = new File("/dev/input");
        File[] event_device_files = LinuxEnvironmentPlugin.listFilesPrivileged(dev, new FilenameFilter(){

            public final boolean accept(File dir, String name) {
                return name.startsWith("event");
            }
        });
        if (event_device_files == null) {
            return;
        }
        for (int i = 0; i < event_device_files.length; ++i) {
            File event_file = event_device_files[i];
            try {
                String path = LinuxEnvironmentPlugin.getAbsolutePathPrivileged(event_file);
                LinuxEventDevice device = new LinuxEventDevice(path);
                try {
                    Controller controller = LinuxEnvironmentPlugin.createControllerFromDevice(device);
                    if (controller != null) {
                        controllers.add(controller);
                        this.devices.add(device);
                        continue;
                    }
                    device.close();
                }
                catch (IOException e) {
                    LinuxEnvironmentPlugin.logln("Failed to create Controller: " + e.getMessage());
                    device.close();
                }
                continue;
            }
            catch (IOException e) {
                LinuxEnvironmentPlugin.logln("Failed to open device (" + event_file + "): " + e.getMessage());
            }
        }
    }

    public boolean isSupported() {
        return supported;
    }

    static {
        String osName = LinuxEnvironmentPlugin.getPrivilegedProperty("os.name", "").trim();
        if (osName.equals("Linux")) {
            supported = true;
            if ("i386".equals(LinuxEnvironmentPlugin.getPrivilegedProperty("os.arch"))) {
                LinuxEnvironmentPlugin.loadLibrary(LIBNAME);
            } else {
                LinuxEnvironmentPlugin.loadLibrary("jinput-linux64");
            }
        }
    }

    private final class ShutdownHook
    extends Thread {
        private ShutdownHook() {
        }

        public final void run() {
            for (int i = 0; i < LinuxEnvironmentPlugin.this.devices.size(); ++i) {
                try {
                    LinuxDevice device = (LinuxDevice)LinuxEnvironmentPlugin.this.devices.get(i);
                    device.close();
                    continue;
                }
                catch (IOException e) {
                    ControllerEnvironment.logln("Failed to close device: " + e.getMessage());
                }
            }
        }
    }
}

/*
 * Decompiled with CFR 0.152.
 */
package net.java.games.input;

import net.java.games.input.LinuxAxisDescriptor;

final class LinuxEvent {
    private long nanos;
    private final LinuxAxisDescriptor descriptor = new LinuxAxisDescriptor();
    private int value;

    LinuxEvent() {
    }

    public final void set(long seconds, long microseconds, int type, int code, int value) {
        this.nanos = (seconds * 1000000L + microseconds) * 1000L;
        this.descriptor.set(type, code);
        this.value = value;
    }

    public final int getValue() {
        return this.value;
    }

    public final LinuxAxisDescriptor getDescriptor() {
        return this.descriptor;
    }

    public final long getNanos() {
        return this.nanos;
    }
}

/*
 * Decompiled with CFR 0.152.
 */
package net.java.games.input;

import java.io.IOException;
import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.LinuxAbsInfo;
import net.java.games.input.LinuxAxisDescriptor;
import net.java.games.input.LinuxEventDevice;
import net.java.games.input.LinuxNativeTypesMap;

final class LinuxEventComponent {
    private final LinuxEventDevice device;
    private final Component.Identifier identifier;
    private final Controller.Type button_trait;
    private final boolean is_relative;
    private final LinuxAxisDescriptor descriptor;
    private final int min;
    private final int max;
    private final int flat;
    static final /* synthetic */ boolean $assertionsDisabled;

    public LinuxEventComponent(LinuxEventDevice device, Component.Identifier identifier, boolean is_relative, int native_type, int native_code) throws IOException {
        this.device = device;
        this.identifier = identifier;
        this.button_trait = native_type == 1 ? LinuxNativeTypesMap.guessButtonTrait(native_code) : Controller.Type.UNKNOWN;
        this.is_relative = is_relative;
        this.descriptor = new LinuxAxisDescriptor();
        this.descriptor.set(native_type, native_code);
        if (native_type == 3) {
            LinuxAbsInfo abs_info = new LinuxAbsInfo();
            this.getAbsInfo(abs_info);
            this.min = abs_info.getMin();
            this.max = abs_info.getMax();
            this.flat = abs_info.getFlat();
        } else {
            this.min = Integer.MIN_VALUE;
            this.max = Integer.MAX_VALUE;
            this.flat = 0;
        }
    }

    public final LinuxEventDevice getDevice() {
        return this.device;
    }

    public final void getAbsInfo(LinuxAbsInfo abs_info) throws IOException {
        if (!$assertionsDisabled && this.descriptor.getType() != 3) {
            throw new AssertionError();
        }
        this.device.getAbsInfo(this.descriptor.getCode(), abs_info);
    }

    public final Controller.Type getButtonTrait() {
        return this.button_trait;
    }

    public final Component.Identifier getIdentifier() {
        return this.identifier;
    }

    public final LinuxAxisDescriptor getDescriptor() {
        return this.descriptor;
    }

    public final boolean isRelative() {
        return this.is_relative;
    }

    public final boolean isAnalog() {
        return this.identifier instanceof Component.Identifier.Axis && this.identifier != Component.Identifier.Axis.POV;
    }

    final float convertValue(float value) {
        if (this.identifier instanceof Component.Identifier.Axis && !this.is_relative) {
            if (this.min == this.max) {
                return 0.0f;
            }
            if (value > (float)this.max) {
                value = this.max;
            } else if (value < (float)this.min) {
                value = this.min;
            }
            return 2.0f * (value - (float)this.min) / (float)(this.max - this.min) - 1.0f;
        }
        return value;
    }

    final float getDeadZone() {
        return (float)this.flat / (2.0f * (float)(this.max - this.min));
    }

    static {
        $assertionsDisabled = !LinuxEventComponent.class.desiredAssertionStatus();
    }
}

/*
 * Decompiled with CFR 0.152.
 */
package net.java.games.input;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.LinuxAbsInfo;
import net.java.games.input.LinuxAxisDescriptor;
import net.java.games.input.LinuxComponent;
import net.java.games.input.LinuxDevice;
import net.java.games.input.LinuxDeviceTask;
import net.java.games.input.LinuxEnvironmentPlugin;
import net.java.games.input.LinuxEvent;
import net.java.games.input.LinuxEventComponent;
import net.java.games.input.LinuxInputID;
import net.java.games.input.LinuxNativeTypesMap;
import net.java.games.input.LinuxRumbleFF;
import net.java.games.input.Rumbler;

final class LinuxEventDevice
implements LinuxDevice {
    private final Map component_map = new HashMap();
    private final Rumbler[] rumblers;
    private final long fd;
    private final String name;
    private final LinuxInputID input_id;
    private final List components;
    private final Controller.Type type;
    private boolean closed;
    private final byte[] key_states = new byte[64];

    public LinuxEventDevice(String filename) throws IOException {
        long fd;
        boolean detect_rumblers = true;
        try {
            fd = LinuxEventDevice.nOpen(filename, true);
        }
        catch (IOException e) {
            fd = LinuxEventDevice.nOpen(filename, false);
            detect_rumblers = false;
        }
        this.fd = fd;
        try {
            this.name = this.getDeviceName();
            this.input_id = this.getDeviceInputID();
            this.components = this.getDeviceComponents();
            this.rumblers = detect_rumblers ? this.enumerateRumblers() : new Rumbler[0];
            this.type = this.guessType();
        }
        catch (IOException e) {
            this.close();
            throw e;
        }
    }

    private static final native long nOpen(String var0, boolean var1) throws IOException;

    public final Controller.Type getType() {
        return this.type;
    }

    private static final int countComponents(List components, Class id_type, boolean relative) {
        int count = 0;
        for (int i = 0; i < components.size(); ++i) {
            LinuxEventComponent component = (LinuxEventComponent)components.get(i);
            if (!id_type.isInstance(component.getIdentifier()) || relative != component.isRelative()) continue;
            ++count;
        }
        return count;
    }

    private final Controller.Type guessType() throws IOException {
        Controller.Type type_from_usages = this.guessTypeFromUsages();
        if (type_from_usages == Controller.Type.UNKNOWN) {
            return this.guessTypeFromComponents();
        }
        return type_from_usages;
    }

    private final Controller.Type guessTypeFromUsages() throws IOException {
        byte[] usage_bits = this.getDeviceUsageBits();
        if (LinuxEventDevice.isBitSet(usage_bits, 0)) {
            return Controller.Type.MOUSE;
        }
        if (LinuxEventDevice.isBitSet(usage_bits, 3)) {
            return Controller.Type.KEYBOARD;
        }
        if (LinuxEventDevice.isBitSet(usage_bits, 2)) {
            return Controller.Type.GAMEPAD;
        }
        if (LinuxEventDevice.isBitSet(usage_bits, 1)) {
            return Controller.Type.STICK;
        }
        return Controller.Type.UNKNOWN;
    }

    private final Controller.Type guessTypeFromComponents() throws IOException {
        List components = this.getComponents();
        if (components.size() == 0) {
            return Controller.Type.UNKNOWN;
        }
        int num_rel_axes = LinuxEventDevice.countComponents(components, Component.Identifier.Axis.class, true);
        int num_abs_axes = LinuxEventDevice.countComponents(components, Component.Identifier.Axis.class, false);
        int num_keys = LinuxEventDevice.countComponents(components, Component.Identifier.Key.class, false);
        int mouse_traits = 0;
        int keyboard_traits = 0;
        int joystick_traits = 0;
        int gamepad_traits = 0;
        if (this.name.toLowerCase().indexOf("mouse") != -1) {
            ++mouse_traits;
        }
        if (this.name.toLowerCase().indexOf("keyboard") != -1) {
            ++keyboard_traits;
        }
        if (this.name.toLowerCase().indexOf("joystick") != -1) {
            ++joystick_traits;
        }
        if (this.name.toLowerCase().indexOf("gamepad") != -1) {
            ++gamepad_traits;
        }
        int num_keyboard_button_traits = 0;
        int num_mouse_button_traits = 0;
        int num_joystick_button_traits = 0;
        int num_gamepad_button_traits = 0;
        for (int i = 0; i < components.size(); ++i) {
            LinuxEventComponent component = (LinuxEventComponent)components.get(i);
            if (component.getButtonTrait() == Controller.Type.MOUSE) {
                ++num_mouse_button_traits;
                continue;
            }
            if (component.getButtonTrait() == Controller.Type.KEYBOARD) {
                ++num_keyboard_button_traits;
                continue;
            }
            if (component.getButtonTrait() == Controller.Type.GAMEPAD) {
                ++num_gamepad_button_traits;
                continue;
            }
            if (component.getButtonTrait() != Controller.Type.STICK) continue;
            ++num_joystick_button_traits;
        }
        if (num_mouse_button_traits >= num_keyboard_button_traits && num_mouse_button_traits >= num_joystick_button_traits && num_mouse_button_traits >= num_gamepad_button_traits) {
            ++mouse_traits;
        } else if (num_keyboard_button_traits >= num_mouse_button_traits && num_keyboard_button_traits >= num_joystick_button_traits && num_keyboard_button_traits >= num_gamepad_button_traits) {
            ++keyboard_traits;
        } else if (num_joystick_button_traits >= num_keyboard_button_traits && num_joystick_button_traits >= num_mouse_button_traits && num_joystick_button_traits >= num_gamepad_button_traits) {
            ++joystick_traits;
        } else if (num_gamepad_button_traits >= num_keyboard_button_traits && num_gamepad_button_traits >= num_mouse_button_traits && num_gamepad_button_traits >= num_joystick_button_traits) {
            ++gamepad_traits;
        }
        if (num_rel_axes >= 2) {
            ++mouse_traits;
        }
        if (num_abs_axes >= 2) {
            ++joystick_traits;
            ++gamepad_traits;
        }
        if (mouse_traits >= keyboard_traits && mouse_traits >= joystick_traits && mouse_traits >= gamepad_traits) {
            return Controller.Type.MOUSE;
        }
        if (keyboard_traits >= mouse_traits && keyboard_traits >= joystick_traits && keyboard_traits >= gamepad_traits) {
            return Controller.Type.KEYBOARD;
        }
        if (joystick_traits >= mouse_traits && joystick_traits >= keyboard_traits && joystick_traits >= gamepad_traits) {
            return Controller.Type.STICK;
        }
        if (gamepad_traits >= mouse_traits && gamepad_traits >= keyboard_traits && gamepad_traits >= joystick_traits) {
            return Controller.Type.GAMEPAD;
        }
        return null;
    }

    private final Rumbler[] enumerateRumblers() {
        ArrayList<LinuxRumbleFF> rumblers = new ArrayList<LinuxRumbleFF>();
        try {
            int num_effects = this.getNumEffects();
            if (num_effects <= 0) {
                return rumblers.toArray(new Rumbler[0]);
            }
            byte[] ff_bits = this.getForceFeedbackBits();
            if (LinuxEventDevice.isBitSet(ff_bits, 80) && num_effects > rumblers.size()) {
                rumblers.add(new LinuxRumbleFF(this));
            }
        }
        catch (IOException e) {
            LinuxEnvironmentPlugin.logln("Failed to enumerate rumblers: " + e.getMessage());
        }
        return rumblers.toArray(new Rumbler[0]);
    }

    public final Rumbler[] getRumblers() {
        return this.rumblers;
    }

    public final synchronized int uploadRumbleEffect(int id, int trigger_button, int direction, int trigger_interval, int replay_length, int replay_delay, int strong_magnitude, int weak_magnitude) throws IOException {
        this.checkClosed();
        return LinuxEventDevice.nUploadRumbleEffect(this.fd, id, direction, trigger_button, trigger_interval, replay_length, replay_delay, strong_magnitude, weak_magnitude);
    }

    private static final native int nUploadRumbleEffect(long var0, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9) throws IOException;

    public final synchronized int uploadConstantEffect(int id, int trigger_button, int direction, int trigger_interval, int replay_length, int replay_delay, int constant_level, int constant_env_attack_length, int constant_env_attack_level, int constant_env_fade_length, int constant_env_fade_level) throws IOException {
        this.checkClosed();
        return LinuxEventDevice.nUploadConstantEffect(this.fd, id, direction, trigger_button, trigger_interval, replay_length, replay_delay, constant_level, constant_env_attack_length, constant_env_attack_level, constant_env_fade_length, constant_env_fade_level);
    }

    private static final native int nUploadConstantEffect(long var0, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, int var10, int var11, int var12) throws IOException;

    final void eraseEffect(int id) throws IOException {
        LinuxEventDevice.nEraseEffect(this.fd, id);
    }

    private static final native void nEraseEffect(long var0, int var2) throws IOException;

    public final synchronized void writeEvent(int type, int code, int value) throws IOException {
        this.checkClosed();
        LinuxEventDevice.nWriteEvent(this.fd, type, code, value);
    }

    private static final native void nWriteEvent(long var0, int var2, int var3, int var4) throws IOException;

    public final void registerComponent(LinuxAxisDescriptor desc, LinuxComponent component) {
        this.component_map.put(desc, component);
    }

    public final LinuxComponent mapDescriptor(LinuxAxisDescriptor desc) {
        return (LinuxComponent)this.component_map.get(desc);
    }

    public final Controller.PortType getPortType() throws IOException {
        return this.input_id.getPortType();
    }

    public final LinuxInputID getInputID() {
        return this.input_id;
    }

    private final LinuxInputID getDeviceInputID() throws IOException {
        return LinuxEventDevice.nGetInputID(this.fd);
    }

    private static final native LinuxInputID nGetInputID(long var0) throws IOException;

    public final int getNumEffects() throws IOException {
        return LinuxEventDevice.nGetNumEffects(this.fd);
    }

    private static final native int nGetNumEffects(long var0) throws IOException;

    private final int getVersion() throws IOException {
        return LinuxEventDevice.nGetVersion(this.fd);
    }

    private static final native int nGetVersion(long var0) throws IOException;

    public final synchronized boolean getNextEvent(LinuxEvent linux_event) throws IOException {
        this.checkClosed();
        return LinuxEventDevice.nGetNextEvent(this.fd, linux_event);
    }

    private static final native boolean nGetNextEvent(long var0, LinuxEvent var2) throws IOException;

    public final synchronized void getAbsInfo(int abs_axis, LinuxAbsInfo abs_info) throws IOException {
        this.checkClosed();
        LinuxEventDevice.nGetAbsInfo(this.fd, abs_axis, abs_info);
    }

    private static final native void nGetAbsInfo(long var0, int var2, LinuxAbsInfo var3) throws IOException;

    private final void addKeys(List components) throws IOException {
        byte[] bits = this.getKeysBits();
        for (int i = 0; i < bits.length * 8; ++i) {
            if (!LinuxEventDevice.isBitSet(bits, i)) continue;
            Component.Identifier id = LinuxNativeTypesMap.getButtonID(i);
            components.add(new LinuxEventComponent(this, id, false, 1, i));
        }
    }

    private final void addAbsoluteAxes(List components) throws IOException {
        byte[] bits = this.getAbsoluteAxesBits();
        for (int i = 0; i < bits.length * 8; ++i) {
            if (!LinuxEventDevice.isBitSet(bits, i)) continue;
            Component.Identifier id = LinuxNativeTypesMap.getAbsAxisID(i);
            components.add(new LinuxEventComponent(this, id, false, 3, i));
        }
    }

    private final void addRelativeAxes(List components) throws IOException {
        byte[] bits = this.getRelativeAxesBits();
        for (int i = 0; i < bits.length * 8; ++i) {
            if (!LinuxEventDevice.isBitSet(bits, i)) continue;
            Component.Identifier id = LinuxNativeTypesMap.getRelAxisID(i);
            components.add(new LinuxEventComponent(this, id, true, 2, i));
        }
    }

    public final List getComponents() {
        return this.components;
    }

    private final List getDeviceComponents() throws IOException {
        ArrayList components = new ArrayList();
        byte[] evtype_bits = this.getEventTypeBits();
        if (LinuxEventDevice.isBitSet(evtype_bits, 1)) {
            this.addKeys(components);
        }
        if (LinuxEventDevice.isBitSet(evtype_bits, 3)) {
            this.addAbsoluteAxes(components);
        }
        if (LinuxEventDevice.isBitSet(evtype_bits, 2)) {
            this.addRelativeAxes(components);
        }
        return components;
    }

    private final byte[] getForceFeedbackBits() throws IOException {
        byte[] bits = new byte[16];
        LinuxEventDevice.nGetBits(this.fd, 21, bits);
        return bits;
    }

    private final byte[] getKeysBits() throws IOException {
        byte[] bits = new byte[64];
        LinuxEventDevice.nGetBits(this.fd, 1, bits);
        return bits;
    }

    private final byte[] getAbsoluteAxesBits() throws IOException {
        byte[] bits = new byte[8];
        LinuxEventDevice.nGetBits(this.fd, 3, bits);
        return bits;
    }

    private final byte[] getRelativeAxesBits() throws IOException {
        byte[] bits = new byte[2];
        LinuxEventDevice.nGetBits(this.fd, 2, bits);
        return bits;
    }

    private final byte[] getEventTypeBits() throws IOException {
        byte[] bits = new byte[4];
        LinuxEventDevice.nGetBits(this.fd, 0, bits);
        return bits;
    }

    private static final native void nGetBits(long var0, int var2, byte[] var3) throws IOException;

    private final byte[] getDeviceUsageBits() throws IOException {
        byte[] bits = new byte[2];
        if (this.getVersion() >= 65537) {
            LinuxEventDevice.nGetDeviceUsageBits(this.fd, bits);
        }
        return bits;
    }

    private static final native void nGetDeviceUsageBits(long var0, byte[] var2) throws IOException;

    public final synchronized void pollKeyStates() throws IOException {
        LinuxEventDevice.nGetKeyStates(this.fd, this.key_states);
    }

    private static final native void nGetKeyStates(long var0, byte[] var2) throws IOException;

    public final boolean isKeySet(int bit) {
        return LinuxEventDevice.isBitSet(this.key_states, bit);
    }

    public static final boolean isBitSet(byte[] bits, int bit) {
        return (bits[bit / 8] & 1 << bit % 8) != 0;
    }

    public final String getName() {
        return this.name;
    }

    private final String getDeviceName() throws IOException {
        return LinuxEventDevice.nGetName(this.fd);
    }

    private static final native String nGetName(long var0) throws IOException;

    public final synchronized void close() throws IOException {
        if (this.closed) {
            return;
        }
        this.closed = true;
        LinuxEnvironmentPlugin.execute(new LinuxDeviceTask(){

            protected final Object execute() throws IOException {
                LinuxEventDevice.nClose(LinuxEventDevice.this.fd);
                return null;
            }
        });
    }

    private static final native void nClose(long var0) throws IOException;

    private final void checkClosed() throws IOException {
        if (this.closed) {
            throw new IOException("Device is closed");
        }
    }

    protected void finalize() throws IOException {
        this.close();
    }
}

/*
 * Decompiled with CFR 0.152.
 */
package net.java.games.input;

import java.io.IOException;
import net.java.games.input.Component;
import net.java.games.input.LinuxDeviceTask;
import net.java.games.input.LinuxEnvironmentPlugin;
import net.java.games.input.LinuxEventDevice;
import net.java.games.input.Rumbler;

abstract class LinuxForceFeedbackEffect
implements Rumbler {
    private final LinuxEventDevice device;
    private final int ff_id;
    private final WriteTask write_task = new WriteTask();
    private final UploadTask upload_task = new UploadTask();

    public LinuxForceFeedbackEffect(LinuxEventDevice device) throws IOException {
        this.device = device;
        this.ff_id = this.upload_task.doUpload(-1, 0.0f);
    }

    protected abstract int upload(int var1, float var2) throws IOException;

    protected final LinuxEventDevice getDevice() {
        return this.device;
    }

    public final synchronized void rumble(float intensity) {
        try {
            if (intensity > 0.0f) {
                this.upload_task.doUpload(this.ff_id, intensity);
                this.write_task.write(1);
            } else {
                this.write_task.write(0);
            }
        }
        catch (IOException e) {
            LinuxEnvironmentPlugin.logln("Failed to rumble: " + e);
        }
    }

    public final String getAxisName() {
        return null;
    }

    public final Component.Identifier getAxisIdentifier() {
        return null;
    }

    private final class WriteTask
    extends LinuxDeviceTask {
        private int value;

        private WriteTask() {
        }

        public final void write(int value) throws IOException {
            this.value = value;
            LinuxEnvironmentPlugin.execute(this);
        }

        protected final Object execute() throws IOException {
            LinuxForceFeedbackEffect.this.device.writeEvent(21, LinuxForceFeedbackEffect.this.ff_id, this.value);
            return null;
        }
    }

    private final class UploadTask
    extends LinuxDeviceTask {
        private int id;
        private float intensity;

        private UploadTask() {
        }

        public final int doUpload(int id, float intensity) throws IOException {
            this.id = id;
            this.intensity = intensity;
            LinuxEnvironmentPlugin.execute(this);
            return this.id;
        }

        protected final Object execute() throws IOException {
            this.id = LinuxForceFeedbackEffect.this.upload(this.id, this.intensity);
            return null;
        }
    }
}

/*
 * Decompiled with CFR 0.152.
 */
package net.java.games.input;

import net.java.games.input.Controller;
import net.java.games.input.LinuxNativeTypesMap;

final class LinuxInputID {
    private final int bustype;
    private final int vendor;
    private final int product;
    private final int version;

    public LinuxInputID(int bustype, int vendor, int product, int version) {
        this.bustype = bustype;
        this.vendor = vendor;
        this.product = product;
        this.version = version;
    }

    public final Controller.PortType getPortType() {
        return LinuxNativeTypesMap.getPortType(this.bustype);
    }

    public final String toString() {
        return "LinuxInputID: bustype = 0x" + Integer.toHexString(this.bustype) + " | vendor = 0x" + Integer.toHexString(this.vendor) + " | product = 0x" + Integer.toHexString(this.product) + " | version = 0x" + Integer.toHexString(this.version);
    }
}

/*
 * Decompiled with CFR 0.152.
 */
package net.java.games.input;

import java.io.IOException;
import net.java.games.input.AbstractController;
import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.Event;
import net.java.games.input.LinuxJoystickDevice;
import net.java.games.input.Rumbler;

final class LinuxJoystickAbstractController
extends AbstractController {
    private final LinuxJoystickDevice device;

    protected LinuxJoystickAbstractController(LinuxJoystickDevice device, Component[] components, Controller[] children, Rumbler[] rumblers) {
        super(device.getName(), components, children, rumblers);
        this.device = device;
    }

    protected final void setDeviceEventQueueSize(int size) throws IOException {
        this.device.setBufferSize(size);
    }

    public final void pollDevice() throws IOException {
        this.device.poll();
    }

    protected final boolean getNextDeviceEvent(Event event) throws IOException {
        return this.device.getNextEvent(event);
    }

    public Controller.Type getType() {
        return Controller.Type.STICK;
    }
}

/*
 * Decompiled with CFR 0.152.
 */
package net.java.games.input;

import java.io.IOException;
import net.java.games.input.AbstractComponent;
import net.java.games.input.Component;

class LinuxJoystickAxis
extends AbstractComponent {
    private float value;
    private boolean analog;

    public LinuxJoystickAxis(Component.Identifier.Axis axis_id) {
        this(axis_id, true);
    }

    public LinuxJoystickAxis(Component.Identifier.Axis axis_id, boolean analog) {
        super(axis_id.getName(), axis_id);
        this.analog = analog;
    }

    public final boolean isRelative() {
        return false;
    }

    public final boolean isAnalog() {
        return this.analog;
    }

    final void setValue(float value) {
        this.value = value;
        this.resetHasPolled();
    }

    protected final float poll() throws IOException {
        return this.value;
    }
}

/*
 * Decompiled with CFR 0.152.
 */
package net.java.games.input;

import java.io.IOException;
import net.java.games.input.AbstractComponent;
import net.java.games.input.Component;

final class LinuxJoystickButton
extends AbstractComponent {
    private float value;

    public LinuxJoystickButton(Component.Identifier button_id) {
        super(button_id.getName(), button_id);
    }

    public final boolean isRelative() {
        return false;
    }

    final void setValue(float value) {
        this.value = value;
    }

    protected final float poll() throws IOException {
        return this.value;
    }
}

/*
 * Decompiled with CFR 0.152.
 */
package net.java.games.input;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import net.java.games.input.Event;
import net.java.games.input.EventQueue;
import net.java.games.input.LinuxDevice;
import net.java.games.input.LinuxJoystickAxis;
import net.java.games.input.LinuxJoystickButton;
import net.java.games.input.LinuxJoystickEvent;
import net.java.games.input.LinuxJoystickPOV;

final class LinuxJoystickDevice
implements LinuxDevice {
    public static final int JS_EVENT_BUTTON = 1;
    public static final int JS_EVENT_AXIS = 2;
    public static final int JS_EVENT_INIT = 128;
    public static final int AXIS_MAX_VALUE = Short.MAX_VALUE;
    private final long fd;
    private final String name;
    private final LinuxJoystickEvent joystick_event = new LinuxJoystickEvent();
    private final Event event = new Event();
    private final LinuxJoystickButton[] buttons;
    private final LinuxJoystickAxis[] axes;
    private final Map povXs = new HashMap();
    private final Map povYs = new HashMap();
    private final byte[] axisMap;
    private final char[] buttonMap;
    private EventQueue event_queue;
    private boolean closed;

    public LinuxJoystickDevice(String filename) throws IOException {
        this.fd = LinuxJoystickDevice.nOpen(filename);
        try {
            this.name = this.getDeviceName();
            this.setBufferSize(32);
            this.buttons = new LinuxJoystickButton[this.getNumDeviceButtons()];
            this.axes = new LinuxJoystickAxis[this.getNumDeviceAxes()];
            this.axisMap = this.getDeviceAxisMap();
            this.buttonMap = this.getDeviceButtonMap();
        }
        catch (IOException e) {
            this.close();
            throw e;
        }
    }

    private static final native long nOpen(String var0) throws IOException;

    public final synchronized void setBufferSize(int size) {
        this.event_queue = new EventQueue(size);
    }

    private final void processEvent(LinuxJoystickEvent joystick_event) {
        int index = joystick_event.getNumber();
        int type = joystick_event.getType() & 0xFFFFFF7F;
        switch (type) {
            case 1: {
                LinuxJoystickButton button;
                if (index < this.getNumButtons() && (button = this.buttons[index]) != null) {
                    float value = joystick_event.getValue();
                    button.setValue(value);
                    this.event.set(button, value, joystick_event.getNanos());
                    break;
                }
                return;
            }
            case 2: {
                LinuxJoystickAxis axis;
                if (index < this.getNumAxes() && (axis = this.axes[index]) != null) {
                    float value = (float)joystick_event.getValue() / 32767.0f;
                    axis.setValue(value);
                    if (this.povXs.containsKey(new Integer(index))) {
                        LinuxJoystickPOV pov = (LinuxJoystickPOV)this.povXs.get(new Integer(index));
                        pov.updateValue();
                        this.event.set(pov, pov.getPollData(), joystick_event.getNanos());
                        break;
                    }
                    if (this.povYs.containsKey(new Integer(index))) {
                        LinuxJoystickPOV pov = (LinuxJoystickPOV)this.povYs.get(new Integer(index));
                        pov.updateValue();
                        this.event.set(pov, pov.getPollData(), joystick_event.getNanos());
                        break;
                    }
                    this.event.set(axis, value, joystick_event.getNanos());
                    break;
                }
                return;
            }
            default: {
                return;
            }
        }
        if (!this.event_queue.isFull()) {
            this.event_queue.add(this.event);
        }
    }

    public final void registerAxis(int index, LinuxJoystickAxis axis) {
        this.axes[index] = axis;
    }

    public final void registerButton(int index, LinuxJoystickButton button) {
        this.buttons[index] = button;
    }

    public final void registerPOV(LinuxJoystickPOV pov) {
        int yIndex;
        int xIndex;
        LinuxJoystickAxis xAxis = pov.getYAxis();
        LinuxJoystickAxis yAxis = pov.getXAxis();
        for (xIndex = 0; xIndex < this.axes.length && this.axes[xIndex] != xAxis; ++xIndex) {
        }
        for (yIndex = 0; yIndex < this.axes.length && this.axes[yIndex] != yAxis; ++yIndex) {
        }
        this.povXs.put(new Integer(xIndex), pov);
        this.povYs.put(new Integer(yIndex), pov);
    }

    public final synchronized boolean getNextEvent(Event event) throws IOException {
        return this.event_queue.getNextEvent(event);
    }

    public final synchronized void poll() throws IOException {
        this.checkClosed();
        while (this.getNextDeviceEvent(this.joystick_event)) {
            this.processEvent(this.joystick_event);
        }
    }

    private final boolean getNextDeviceEvent(LinuxJoystickEvent joystick_event) throws IOException {
        return LinuxJoystickDevice.nGetNextEvent(this.fd, joystick_event);
    }

    private static final native boolean nGetNextEvent(long var0, LinuxJoystickEvent var2) throws IOException;

    public final int getNumAxes() {
        return this.axes.length;
    }

    public final int getNumButtons() {
        return this.buttons.length;
    }

    public final byte[] getAxisMap() {
        return this.axisMap;
    }

    public final char[] getButtonMap() {
        return this.buttonMap;
    }

    private final int getNumDeviceButtons() throws IOException {
        return LinuxJoystickDevice.nGetNumButtons(this.fd);
    }

    private static final native int nGetNumButtons(long var0) throws IOException;

    private final int getNumDeviceAxes() throws IOException {
        return LinuxJoystickDevice.nGetNumAxes(this.fd);
    }

    private static final native int nGetNumAxes(long var0) throws IOException;

    private final byte[] getDeviceAxisMap() throws IOException {
        return LinuxJoystickDevice.nGetAxisMap(this.fd);
    }

    private static final native byte[] nGetAxisMap(long var0) throws IOException;

    private final char[] getDeviceButtonMap() throws IOException {
        return LinuxJoystickDevice.nGetButtonMap(this.fd);
    }

    private static final native char[] nGetButtonMap(long var0) throws IOException;

    private final int getVersion() throws IOException {
        return LinuxJoystickDevice.nGetVersion(this.fd);
    }

    private static final native int nGetVersion(long var0) throws IOException;

    public final String getName() {
        return this.name;
    }

    private final String getDeviceName() throws IOException {
        return LinuxJoystickDevice.nGetName(this.fd);
    }

    private static final native String nGetName(long var0) throws IOException;

    public final synchronized void close() throws IOException {
        if (!this.closed) {
            this.closed = true;
            LinuxJoystickDevice.nClose(this.fd);
        }
    }

    private static final native void nClose(long var0) throws IOException;

    private final void checkClosed() throws IOException {
        if (this.closed) {
            throw new IOException("Device is closed");
        }
    }

    protected void finalize() throws IOException {
        this.close();
    }
}

/*
 * Decompiled with CFR 0.152.
 */
package net.java.games.input;

final class LinuxJoystickEvent {
    private long nanos;
    private int value;
    private int type;
    private int number;

    LinuxJoystickEvent() {
    }

    public final void set(long millis, int value, int type, int number) {
        this.nanos = millis * 1000000L;
        this.value = value;
        this.type = type;
        this.number = number;
    }

    public final int getValue() {
        return this.value;
    }

    public final int getType() {
        return this.type;
    }

    public final int getNumber() {
        return this.number;
    }

    public final long getNanos() {
        return this.nanos;
    }
}

/*
 * Decompiled with CFR 0.152.
 */
package net.java.games.input;

import net.java.games.input.Component;
import net.java.games.input.LinuxEnvironmentPlugin;
import net.java.games.input.LinuxJoystickAxis;

public class LinuxJoystickPOV
extends LinuxJoystickAxis {
    private LinuxJoystickAxis hatX;
    private LinuxJoystickAxis hatY;

    LinuxJoystickPOV(Component.Identifier.Axis id, LinuxJoystickAxis hatX, LinuxJoystickAxis hatY) {
        super(id, false);
        this.hatX = hatX;
        this.hatY = hatY;
    }

    protected LinuxJoystickAxis getXAxis() {
        return this.hatX;
    }

    protected LinuxJoystickAxis getYAxis() {
        return this.hatY;
    }

    protected void updateValue() {
        float last_x = this.hatX.getPollData();
        float last_y = this.hatY.getPollData();
        this.resetHasPolled();
        if (last_x == -1.0f && last_y == -1.0f) {
            this.setValue(0.125f);
        } else if (last_x == -1.0f && last_y == 0.0f) {
            this.setValue(1.0f);
        } else if (last_x == -1.0f && last_y == 1.0f) {
            this.setValue(0.875f);
        } else if (last_x == 0.0f && last_y == -1.0f) {
            this.setValue(0.25f);
        } else if (last_x == 0.0f && last_y == 0.0f) {
            this.setValue(0.0f);
        } else if (last_x == 0.0f && last_y == 1.0f) {
            this.setValue(0.75f);
        } else if (last_x == 1.0f && last_y == -1.0f) {
            this.setValue(0.375f);
        } else if (last_x == 1.0f && last_y == 0.0f) {
            this.setValue(0.5f);
        } else if (last_x == 1.0f && last_y == 1.0f) {
            this.setValue(0.625f);
        } else {
            LinuxEnvironmentPlugin.logln("Unknown values x = " + last_x + " | y = " + last_y);
            this.setValue(0.0f);
        }
    }
}

/*
 * Decompiled with CFR 0.152.
 */
package net.java.games.input;

import java.io.IOException;
import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.Event;
import net.java.games.input.Keyboard;
import net.java.games.input.LinuxControllers;
import net.java.games.input.LinuxEventDevice;
import net.java.games.input.Rumbler;

final class LinuxKeyboard
extends Keyboard {
    private final Controller.PortType port;
    private final LinuxEventDevice device;

    protected LinuxKeyboard(LinuxEventDevice device, Component[] components, Controller[] children, Rumbler[] rumblers) throws IOException {
        super(device.getName(), components, children, rumblers);
        this.device = device;
        this.port = device.getPortType();
    }

    public final Controller.PortType getPortType() {
        return this.port;
    }

    protected final boolean getNextDeviceEvent(Event event) throws IOException {
        return LinuxControllers.getNextDeviceEvent(event, this.device);
    }

    public final void pollDevice() throws IOException {
        this.device.pollKeyStates();
    }
}

/*
 * Decompiled with CFR 0.152.
 */
package net.java.games.input;

import java.io.IOException;
import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.Event;
import net.java.games.input.LinuxControllers;
import net.java.games.input.LinuxEventDevice;
import net.java.games.input.Mouse;
import net.java.games.input.Rumbler;

final class LinuxMouse
extends Mouse {
    private final Controller.PortType port;
    private final LinuxEventDevice device;

    protected LinuxMouse(LinuxEventDevice device, Component[] components, Controller[] children, Rumbler[] rumblers) throws IOException {
        super(device.getName(), components, children, rumblers);
        this.device = device;
        this.port = device.getPortType();
    }

    public final Controller.PortType getPortType() {
        return this.port;
    }

    public final void pollDevice() throws IOException {
        this.device.pollKeyStates();
    }

    protected final boolean getNextDeviceEvent(Event event) throws IOException {
        return LinuxControllers.getNextDeviceEvent(event, this.device);
    }
}

/*
 * Decompiled with CFR 0.152.
 */
package net.java.games.input;

import java.util.logging.Logger;
import net.java.games.input.Component;
import net.java.games.input.Controller;

class LinuxNativeTypesMap {
    private static LinuxNativeTypesMap INSTANCE = new LinuxNativeTypesMap();
    private static Logger log = Logger.getLogger(LinuxNativeTypesMap.class.getName());
    private final Component.Identifier[] relAxesIDs;
    private final Component.Identifier[] absAxesIDs;
    private final Component.Identifier[] buttonIDs = new Component.Identifier[511];

    private LinuxNativeTypesMap() {
        this.relAxesIDs = new Component.Identifier[15];
        this.absAxesIDs = new Component.Identifier[63];
        this.reInit();
    }

    private void reInit() {
        this.buttonIDs[1] = Component.Identifier.Key.ESCAPE;
        this.buttonIDs[2] = Component.Identifier.Key._1;
        this.buttonIDs[3] = Component.Identifier.Key._2;
        this.buttonIDs[4] = Component.Identifier.Key._3;
        this.buttonIDs[5] = Component.Identifier.Key._4;
        this.buttonIDs[6] = Component.Identifier.Key._5;
        this.buttonIDs[7] = Component.Identifier.Key._6;
        this.buttonIDs[8] = Component.Identifier.Key._7;
        this.buttonIDs[9] = Component.Identifier.Key._8;
        this.buttonIDs[10] = Component.Identifier.Key._9;
        this.buttonIDs[11] = Component.Identifier.Key._0;
        this.buttonIDs[12] = Component.Identifier.Key.MINUS;
        this.buttonIDs[13] = Component.Identifier.Key.EQUALS;
        this.buttonIDs[14] = Component.Identifier.Key.BACK;
        this.buttonIDs[15] = Component.Identifier.Key.TAB;
        this.buttonIDs[16] = Component.Identifier.Key.Q;
        this.buttonIDs[17] = Component.Identifier.Key.W;
        this.buttonIDs[18] = Component.Identifier.Key.E;
        this.buttonIDs[19] = Component.Identifier.Key.R;
        this.buttonIDs[20] = Component.Identifier.Key.T;
        this.buttonIDs[21] = Component.Identifier.Key.Y;
        this.buttonIDs[22] = Component.Identifier.Key.U;
        this.buttonIDs[23] = Component.Identifier.Key.I;
        this.buttonIDs[24] = Component.Identifier.Key.O;
        this.buttonIDs[25] = Component.Identifier.Key.P;
        this.buttonIDs[26] = Component.Identifier.Key.LBRACKET;
        this.buttonIDs[27] = Component.Identifier.Key.RBRACKET;
        this.buttonIDs[28] = Component.Identifier.Key.RETURN;
        this.buttonIDs[29] = Component.Identifier.Key.LCONTROL;
        this.buttonIDs[30] = Component.Identifier.Key.A;
        this.buttonIDs[31] = Component.Identifier.Key.S;
        this.buttonIDs[32] = Component.Identifier.Key.D;
        this.buttonIDs[33] = Component.Identifier.Key.F;
        this.buttonIDs[34] = Component.Identifier.Key.G;
        this.buttonIDs[35] = Component.Identifier.Key.H;
        this.buttonIDs[36] = Component.Identifier.Key.J;
        this.buttonIDs[37] = Component.Identifier.Key.K;
        this.buttonIDs[38] = Component.Identifier.Key.L;
        this.buttonIDs[39] = Component.Identifier.Key.SEMICOLON;
        this.buttonIDs[40] = Component.Identifier.Key.APOSTROPHE;
        this.buttonIDs[41] = Component.Identifier.Key.GRAVE;
        this.buttonIDs[42] = Component.Identifier.Key.LSHIFT;
        this.buttonIDs[43] = Component.Identifier.Key.BACKSLASH;
        this.buttonIDs[44] = Component.Identifier.Key.Z;
        this.buttonIDs[45] = Component.Identifier.Key.X;
        this.buttonIDs[46] = Component.Identifier.Key.C;
        this.buttonIDs[47] = Component.Identifier.Key.V;
        this.buttonIDs[48] = Component.Identifier.Key.B;
        this.buttonIDs[49] = Component.Identifier.Key.N;
        this.buttonIDs[50] = Component.Identifier.Key.M;
        this.buttonIDs[51] = Component.Identifier.Key.COMMA;
        this.buttonIDs[52] = Component.Identifier.Key.PERIOD;
        this.buttonIDs[53] = Component.Identifier.Key.SLASH;
        this.buttonIDs[54] = Component.Identifier.Key.RSHIFT;
        this.buttonIDs[55] = Component.Identifier.Key.MULTIPLY;
        this.buttonIDs[56] = Component.Identifier.Key.LALT;
        this.buttonIDs[57] = Component.Identifier.Key.SPACE;
        this.buttonIDs[58] = Component.Identifier.Key.CAPITAL;
        this.buttonIDs[59] = Component.Identifier.Key.F1;
        this.buttonIDs[60] = Component.Identifier.Key.F2;
        this.buttonIDs[61] = Component.Identifier.Key.F3;
        this.buttonIDs[62] = Component.Identifier.Key.F4;
        this.buttonIDs[63] = Component.Identifier.Key.F5;
        this.buttonIDs[64] = Component.Identifier.Key.F6;
        this.buttonIDs[65] = Component.Identifier.Key.F7;
        this.buttonIDs[66] = Component.Identifier.Key.F8;
        this.buttonIDs[67] = Component.Identifier.Key.F9;
        this.buttonIDs[68] = Component.Identifier.Key.F10;
        this.buttonIDs[69] = Component.Identifier.Key.NUMLOCK;
        this.buttonIDs[70] = Component.Identifier.Key.SCROLL;
        this.buttonIDs[71] = Component.Identifier.Key.NUMPAD7;
        this.buttonIDs[72] = Component.Identifier.Key.NUMPAD8;
        this.buttonIDs[73] = Component.Identifier.Key.NUMPAD9;
        this.buttonIDs[74] = Component.Identifier.Key.SUBTRACT;
        this.buttonIDs[75] = Component.Identifier.Key.NUMPAD4;
        this.buttonIDs[76] = Component.Identifier.Key.NUMPAD5;
        this.buttonIDs[77] = Component.Identifier.Key.NUMPAD6;
        this.buttonIDs[78] = Component.Identifier.Key.ADD;
        this.buttonIDs[79] = Component.Identifier.Key.NUMPAD1;
        this.buttonIDs[80] = Component.Identifier.Key.NUMPAD2;
        this.buttonIDs[81] = Component.Identifier.Key.NUMPAD3;
        this.buttonIDs[82] = Component.Identifier.Key.NUMPAD0;
        this.buttonIDs[83] = Component.Identifier.Key.DECIMAL;
        this.buttonIDs[183] = Component.Identifier.Key.F13;
        this.buttonIDs[86] = null;
        this.buttonIDs[87] = Component.Identifier.Key.F11;
        this.buttonIDs[88] = Component.Identifier.Key.F12;
        this.buttonIDs[184] = Component.Identifier.Key.F14;
        this.buttonIDs[185] = Component.Identifier.Key.F15;
        this.buttonIDs[186] = null;
        this.buttonIDs[187] = null;
        this.buttonIDs[188] = null;
        this.buttonIDs[189] = null;
        this.buttonIDs[190] = null;
        this.buttonIDs[96] = Component.Identifier.Key.NUMPADENTER;
        this.buttonIDs[97] = Component.Identifier.Key.RCONTROL;
        this.buttonIDs[98] = Component.Identifier.Key.DIVIDE;
        this.buttonIDs[99] = Component.Identifier.Key.SYSRQ;
        this.buttonIDs[100] = Component.Identifier.Key.RALT;
        this.buttonIDs[101] = null;
        this.buttonIDs[102] = Component.Identifier.Key.HOME;
        this.buttonIDs[103] = Component.Identifier.Key.UP;
        this.buttonIDs[104] = Component.Identifier.Key.PAGEUP;
        this.buttonIDs[105] = Component.Identifier.Key.LEFT;
        this.buttonIDs[106] = Component.Identifier.Key.RIGHT;
        this.buttonIDs[107] = Component.Identifier.Key.END;
        this.buttonIDs[108] = Component.Identifier.Key.DOWN;
        this.buttonIDs[109] = Component.Identifier.Key.PAGEDOWN;
        this.buttonIDs[110] = Component.Identifier.Key.INSERT;
        this.buttonIDs[111] = Component.Identifier.Key.DELETE;
        this.buttonIDs[119] = Component.Identifier.Key.PAUSE;
        this.buttonIDs[117] = Component.Identifier.Key.NUMPADEQUAL;
        this.buttonIDs[142] = Component.Identifier.Key.SLEEP;
        this.buttonIDs[240] = Component.Identifier.Key.UNLABELED;
        this.buttonIDs[256] = Component.Identifier.Button._0;
        this.buttonIDs[257] = Component.Identifier.Button._1;
        this.buttonIDs[258] = Component.Identifier.Button._2;
        this.buttonIDs[259] = Component.Identifier.Button._3;
        this.buttonIDs[260] = Component.Identifier.Button._4;
        this.buttonIDs[261] = Component.Identifier.Button._5;
        this.buttonIDs[262] = Component.Identifier.Button._6;
        this.buttonIDs[263] = Component.Identifier.Button._7;
        this.buttonIDs[264] = Component.Identifier.Button._8;
        this.buttonIDs[265] = Component.Identifier.Button._9;
        this.buttonIDs[272] = Component.Identifier.Button.LEFT;
        this.buttonIDs[273] = Component.Identifier.Button.RIGHT;
        this.buttonIDs[274] = Component.Identifier.Button.MIDDLE;
        this.buttonIDs[275] = Component.Identifier.Button.SIDE;
        this.buttonIDs[276] = Component.Identifier.Button.EXTRA;
        this.buttonIDs[277] = Component.Identifier.Button.FORWARD;
        this.buttonIDs[278] = Component.Identifier.Button.BACK;
        this.buttonIDs[288] = Component.Identifier.Button.TRIGGER;
        this.buttonIDs[289] = Component.Identifier.Button.THUMB;
        this.buttonIDs[290] = Component.Identifier.Button.THUMB2;
        this.buttonIDs[291] = Component.Identifier.Button.TOP;
        this.buttonIDs[292] = Component.Identifier.Button.TOP2;
        this.buttonIDs[293] = Component.Identifier.Button.PINKIE;
        this.buttonIDs[294] = Component.Identifier.Button.BASE;
        this.buttonIDs[295] = Component.Identifier.Button.BASE2;
        this.buttonIDs[296] = Component.Identifier.Button.BASE3;
        this.buttonIDs[297] = Component.Identifier.Button.BASE4;
        this.buttonIDs[298] = Component.Identifier.Button.BASE5;
        this.buttonIDs[299] = Component.Identifier.Button.BASE6;
        this.buttonIDs[303] = Component.Identifier.Button.DEAD;
        this.buttonIDs[304] = Component.Identifier.Button.A;
        this.buttonIDs[305] = Component.Identifier.Button.B;
        this.buttonIDs[306] = Component.Identifier.Button.C;
        this.buttonIDs[307] = Component.Identifier.Button.X;
        this.buttonIDs[308] = Component.Identifier.Button.Y;
        this.buttonIDs[309] = Component.Identifier.Button.Z;
        this.buttonIDs[310] = Component.Identifier.Button.LEFT_THUMB;
        this.buttonIDs[311] = Component.Identifier.Button.RIGHT_THUMB;
        this.buttonIDs[312] = Component.Identifier.Button.LEFT_THUMB2;
        this.buttonIDs[313] = Component.Identifier.Button.RIGHT_THUMB2;
        this.buttonIDs[314] = Component.Identifier.Button.SELECT;
        this.buttonIDs[316] = Component.Identifier.Button.MODE;
        this.buttonIDs[317] = Component.Identifier.Button.LEFT_THUMB3;
        this.buttonIDs[318] = Component.Identifier.Button.RIGHT_THUMB3;
        this.buttonIDs[320] = Component.Identifier.Button.TOOL_PEN;
        this.buttonIDs[321] = Component.Identifier.Button.TOOL_RUBBER;
        this.buttonIDs[322] = Component.Identifier.Button.TOOL_BRUSH;
        this.buttonIDs[323] = Component.Identifier.Button.TOOL_PENCIL;
        this.buttonIDs[324] = Component.Identifier.Button.TOOL_AIRBRUSH;
        this.buttonIDs[325] = Component.Identifier.Button.TOOL_FINGER;
        this.buttonIDs[326] = Component.Identifier.Button.TOOL_MOUSE;
        this.buttonIDs[327] = Component.Identifier.Button.TOOL_LENS;
        this.buttonIDs[330] = Component.Identifier.Button.TOUCH;
        this.buttonIDs[331] = Component.Identifier.Button.STYLUS;
        this.buttonIDs[332] = Component.Identifier.Button.STYLUS2;
        this.relAxesIDs[0] = Component.Identifier.Axis.X;
        this.relAxesIDs[1] = Component.Identifier.Axis.Y;
        this.relAxesIDs[2] = Component.Identifier.Axis.Z;
        this.relAxesIDs[8] = Component.Identifier.Axis.Z;
        this.relAxesIDs[6] = Component.Identifier.Axis.SLIDER;
        this.relAxesIDs[7] = Component.Identifier.Axis.SLIDER;
        this.relAxesIDs[9] = Component.Identifier.Axis.SLIDER;
        this.absAxesIDs[0] = Component.Identifier.Axis.X;
        this.absAxesIDs[1] = Component.Identifier.Axis.Y;
        this.absAxesIDs[2] = Component.Identifier.Axis.Z;
        this.absAxesIDs[3] = Component.Identifier.Axis.RX;
        this.absAxesIDs[4] = Component.Identifier.Axis.RY;
        this.absAxesIDs[5] = Component.Identifier.Axis.RZ;
        this.absAxesIDs[6] = Component.Identifier.Axis.SLIDER;
        this.absAxesIDs[7] = Component.Identifier.Axis.RZ;
        this.absAxesIDs[8] = Component.Identifier.Axis.Y;
        this.absAxesIDs[9] = Component.Identifier.Axis.SLIDER;
        this.absAxesIDs[10] = Component.Identifier.Axis.SLIDER;
        this.absAxesIDs[16] = Component.Identifier.Axis.POV;
        this.absAxesIDs[17] = Component.Identifier.Axis.POV;
        this.absAxesIDs[18] = Component.Identifier.Axis.POV;
        this.absAxesIDs[19] = Component.Identifier.Axis.POV;
        this.absAxesIDs[20] = Component.Identifier.Axis.POV;
        this.absAxesIDs[21] = Component.Identifier.Axis.POV;
        this.absAxesIDs[22] = Component.Identifier.Axis.POV;
        this.absAxesIDs[23] = Component.Identifier.Axis.POV;
        this.absAxesIDs[24] = null;
        this.absAxesIDs[25] = null;
        this.absAxesIDs[26] = null;
        this.absAxesIDs[27] = null;
        this.absAxesIDs[40] = null;
    }

    public static final Controller.Type guessButtonTrait(int button_code) {
        switch (button_code) {
            case 288:
            case 289:
            case 290:
            case 291:
            case 292:
            case 293:
            case 294:
            case 295:
            case 296:
            case 297:
            case 298:
            case 299:
            case 303: {
                return Controller.Type.STICK;
            }
            case 304:
            case 305:
            case 306:
            case 307:
            case 308:
            case 309:
            case 310:
            case 311:
            case 312:
            case 313:
            case 314:
            case 316:
            case 317:
            case 318: {
                return Controller.Type.GAMEPAD;
            }
            case 256:
            case 257:
            case 258:
            case 259:
            case 260:
            case 261:
            case 262:
            case 263:
            case 264:
            case 265: {
                return Controller.Type.UNKNOWN;
            }
            case 272:
            case 273:
            case 274:
            case 275:
            case 276: {
                return Controller.Type.MOUSE;
            }
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
            case 16:
            case 17:
            case 18:
            case 19:
            case 20:
            case 21:
            case 22:
            case 23:
            case 24:
            case 25:
            case 26:
            case 27:
            case 28:
            case 29:
            case 30:
            case 31:
            case 32:
            case 33:
            case 34:
            case 35:
            case 36:
            case 37:
            case 38:
            case 39:
            case 40:
            case 41:
            case 42:
            case 43:
            case 44:
            case 45:
            case 46:
            case 47:
            case 48:
            case 49:
            case 50:
            case 51:
            case 52:
            case 53:
            case 54:
            case 55:
            case 56:
            case 57:
            case 58:
            case 59:
            case 60:
            case 61:
            case 62:
            case 63:
            case 64:
            case 65:
            case 66:
            case 67:
            case 68:
            case 69:
            case 70:
            case 71:
            case 72:
            case 73:
            case 74:
            case 75:
            case 76:
            case 77:
            case 78:
            case 79:
            case 80:
            case 81:
            case 82:
            case 83:
            case 85:
            case 86:
            case 87:
            case 88:
            case 89:
            case 90:
            case 91:
            case 92:
            case 93:
            case 94:
            case 95:
            case 96:
            case 97:
            case 98:
            case 99:
            case 100:
            case 101:
            case 102:
            case 103:
            case 104:
            case 105:
            case 106:
            case 107:
            case 108:
            case 109:
            case 110:
            case 111:
            case 112:
            case 113:
            case 114:
            case 115:
            case 116:
            case 117:
            case 118:
            case 119:
            case 121:
            case 122:
            case 123:
            case 124:
            case 125:
            case 126:
            case 127:
            case 128:
            case 129:
            case 130:
            case 131:
            case 132:
            case 133:
            case 134:
            case 135:
            case 136:
            case 137:
            case 138:
            case 139:
            case 140:
            case 141:
            case 142:
            case 143:
            case 144:
            case 145:
            case 146:
            case 147:
            case 148:
            case 149:
            case 150:
            case 151:
            case 152:
            case 153:
            case 154:
            case 155:
            case 156:
            case 157:
            case 158:
            case 159:
            case 160:
            case 161:
            case 162:
            case 163:
            case 164:
            case 165:
            case 166:
            case 167:
            case 168:
            case 169:
            case 170:
            case 171:
            case 172:
            case 173:
            case 174:
            case 175:
            case 176:
            case 177:
            case 178:
            case 179:
            case 180:
            case 183:
            case 184:
            case 185:
            case 186:
            case 187:
            case 188:
            case 189:
            case 190:
            case 191:
            case 192:
            case 193:
            case 194:
            case 200:
            case 201:
            case 202:
            case 203:
            case 205:
            case 206:
            case 207:
            case 208:
            case 209:
            case 210:
            case 211:
            case 212:
            case 213:
            case 214:
            case 215:
            case 216:
            case 217:
            case 218:
            case 219:
            case 220:
            case 221:
            case 222:
            case 223:
            case 224:
            case 225:
            case 226:
            case 227:
            case 228:
            case 229:
            case 230:
            case 352:
            case 353:
            case 354:
            case 355:
            case 356:
            case 357:
            case 358:
            case 359:
            case 360:
            case 361:
            case 362:
            case 363:
            case 364:
            case 365:
            case 366:
            case 367:
            case 368:
            case 369:
            case 370:
            case 371:
            case 372:
            case 373:
            case 374:
            case 375:
            case 376:
            case 377:
            case 378:
            case 379:
            case 380:
            case 381:
            case 382:
            case 383:
            case 384:
            case 385:
            case 386:
            case 387:
            case 388:
            case 389:
            case 390:
            case 391:
            case 392:
            case 393:
            case 394:
            case 395:
            case 396:
            case 397:
            case 398:
            case 399:
            case 400:
            case 401:
            case 402:
            case 403:
            case 404:
            case 405:
            case 406:
            case 407:
            case 408:
            case 409:
            case 410:
            case 411:
            case 412:
            case 413:
            case 414:
            case 415:
            case 448:
            case 449:
            case 450:
            case 451:
            case 464:
            case 465:
            case 466:
            case 467:
            case 468:
            case 469:
            case 470:
            case 471:
            case 472:
            case 473:
            case 474:
            case 475:
            case 476:
            case 477:
            case 478:
            case 479:
            case 480:
            case 481:
            case 482:
            case 483:
            case 484: {
                return Controller.Type.KEYBOARD;
            }
        }
        return Controller.Type.UNKNOWN;
    }

    public static Controller.PortType getPortType(int nativeid) {
        switch (nativeid) {
            case 20: {
                return Controller.PortType.GAME;
            }
            case 17: {
                return Controller.PortType.I8042;
            }
            case 21: {
                return Controller.PortType.PARALLEL;
            }
            case 19: {
                return Controller.PortType.SERIAL;
            }
            case 3: {
                return Controller.PortType.USB;
            }
        }
        return Controller.PortType.UNKNOWN;
    }

    public static Component.Identifier getRelAxisID(int nativeID) {
        Component.Identifier retval = null;
        try {
            retval = LinuxNativeTypesMap.INSTANCE.relAxesIDs[nativeID];
        }
        catch (ArrayIndexOutOfBoundsException e) {
            log.warning("INSTANCE.relAxesIDis only " + LinuxNativeTypesMap.INSTANCE.relAxesIDs.length + " long, so " + nativeID + " not contained");
        }
        if (retval == null) {
            retval = Component.Identifier.Axis.SLIDER_VELOCITY;
        }
        return retval;
    }

    public static Component.Identifier getAbsAxisID(int nativeID) {
        Component.Identifier retval = null;
        try {
            retval = LinuxNativeTypesMap.INSTANCE.absAxesIDs[nativeID];
        }
        catch (ArrayIndexOutOfBoundsException e) {
            log.warning("INSTANCE.absAxesIDs is only " + LinuxNativeTypesMap.INSTANCE.absAxesIDs.length + " long, so " + nativeID + " not contained");
        }
        if (retval == null) {
            retval = Component.Identifier.Axis.SLIDER;
        }
        return retval;
    }

    public static Component.Identifier getButtonID(int nativeID) {
        Component.Identifier retval = null;
        try {
            retval = LinuxNativeTypesMap.INSTANCE.buttonIDs[nativeID];
        }
        catch (ArrayIndexOutOfBoundsException e) {
            log.warning("INSTANCE.buttonIDs is only " + LinuxNativeTypesMap.INSTANCE.buttonIDs.length + " long, so " + nativeID + " not contained");
        }
        if (retval == null) {
            retval = Component.Identifier.Key.UNKNOWN;
        }
        return retval;
    }
}

/*
 * Decompiled with CFR 0.152.
 */
package net.java.games.input;

import java.io.IOException;
import net.java.games.input.LinuxAxisDescriptor;
import net.java.games.input.LinuxComponent;
import net.java.games.input.LinuxControllers;
import net.java.games.input.LinuxEnvironmentPlugin;
import net.java.games.input.LinuxEventComponent;

final class LinuxPOV
extends LinuxComponent {
    private final LinuxEventComponent component_x;
    private final LinuxEventComponent component_y;
    private float last_x;
    private float last_y;

    public LinuxPOV(LinuxEventComponent component_x, LinuxEventComponent component_y) {
        super(component_x);
        this.component_x = component_x;
        this.component_y = component_y;
    }

    protected final float poll() throws IOException {
        this.last_x = LinuxControllers.poll(this.component_x);
        this.last_y = LinuxControllers.poll(this.component_y);
        return this.convertValue(0.0f, null);
    }

    public float convertValue(float value, LinuxAxisDescriptor descriptor) {
        if (descriptor == this.component_x.getDescriptor()) {
            this.last_x = value;
        }
        if (descriptor == this.component_y.getDescriptor()) {
            this.last_y = value;
        }
        if (this.last_x == -1.0f && this.last_y == -1.0f) {
            return 0.125f;
        }
        if (this.last_x == -1.0f && this.last_y == 0.0f) {
            return 1.0f;
        }
        if (this.last_x == -1.0f && this.last_y == 1.0f) {
            return 0.875f;
        }
        if (this.last_x == 0.0f && this.last_y == -1.0f) {
            return 0.25f;
        }
        if (this.last_x == 0.0f && this.last_y == 0.0f) {
            return 0.0f;
        }
        if (this.last_x == 0.0f && this.last_y == 1.0f) {
            return 0.75f;
        }
        if (this.last_x == 1.0f && this.last_y == -1.0f) {
            return 0.375f;
        }
        if (this.last_x == 1.0f && this.last_y == 0.0f) {
            return 0.5f;
        }
        if (this.last_x == 1.0f && this.last_y == 1.0f) {
            return 0.625f;
        }
        LinuxEnvironmentPlugin.logln("Unknown values x = " + this.last_x + " | y = " + this.last_y);
        return 0.0f;
    }
}

/*
 * Decompiled with CFR 0.152.
 */
package net.java.games.input;

import java.io.IOException;
import net.java.games.input.LinuxEventDevice;
import net.java.games.input.LinuxForceFeedbackEffect;

final class LinuxRumbleFF
extends LinuxForceFeedbackEffect {
    public LinuxRumbleFF(LinuxEventDevice device) throws IOException {
        super(device);
    }

    protected final int upload(int id, float intensity) throws IOException {
        int weak_magnitude;
        int strong_magnitude;
        if (intensity > 0.666666f) {
            strong_magnitude = (int)(32768.0f * intensity);
            weak_magnitude = (int)(49152.0f * intensity);
        } else if (intensity > 0.3333333f) {
            strong_magnitude = (int)(32768.0f * intensity);
            weak_magnitude = 0;
        } else {
            strong_magnitude = 0;
            weak_magnitude = (int)(49152.0f * intensity);
        }
        return this.getDevice().uploadRumbleEffect(id, 0, 0, 0, -1, 0, strong_magnitude, weak_magnitude);
    }
}

/*
 * Decompiled with CFR 0.152.
 */
package net.java.games.input;

import net.java.games.input.AbstractController;
import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.Rumbler;

public abstract class Mouse
extends AbstractController {
    protected Mouse(String name, Component[] components, Controller[] children, Rumbler[] rumblers) {
        super(name, components, children, rumblers);
    }

    public Controller.Type getType() {
        return Controller.Type.MOUSE;
    }

    public Component getX() {
        return this.getComponent(Component.Identifier.Axis.X);
    }

    public Component getY() {
        return this.getComponent(Component.Identifier.Axis.Y);
    }

    public Component getWheel() {
        return this.getComponent(Component.Identifier.Axis.Z);
    }

    public Component getPrimaryButton() {
        Component primaryButton = this.getComponent(Component.Identifier.Button.LEFT);
        if (primaryButton == null) {
            primaryButton = this.getComponent(Component.Identifier.Button._1);
        }
        return primaryButton;
    }

    public Component getSecondaryButton() {
        Component secondaryButton = this.getComponent(Component.Identifier.Button.RIGHT);
        if (secondaryButton == null) {
            secondaryButton = this.getComponent(Component.Identifier.Button._2);
        }
        return secondaryButton;
    }

    public Component getTertiaryButton() {
        Component tertiaryButton = this.getComponent(Component.Identifier.Button.MIDDLE);
        if (tertiaryButton == null) {
            tertiaryButton = this.getComponent(Component.Identifier.Button._3);
        }
        return tertiaryButton;
    }

    public Component getLeft() {
        return this.getComponent(Component.Identifier.Button.LEFT);
    }

    public Component getRight() {
        return this.getComponent(Component.Identifier.Button.RIGHT);
    }

    public Component getMiddle() {
        return this.getComponent(Component.Identifier.Button.MIDDLE);
    }

    public Component getSide() {
        return this.getComponent(Component.Identifier.Button.SIDE);
    }

    public Component getExtra() {
        return this.getComponent(Component.Identifier.Button.EXTRA);
    }

    public Component getForward() {
        return this.getComponent(Component.Identifier.Button.FORWARD);
    }

    public Component getBack() {
        return this.getComponent(Component.Identifier.Button.BACK);
    }

    public Component getButton3() {
        return this.getComponent(Component.Identifier.Button._3);
    }

    public Component getButton4() {
        return this.getComponent(Component.Identifier.Button._4);
    }
}

/*
 * Decompiled with CFR 0.152.
 */
package net.java.games.input;

class NativeDefinitions {
    public static final int EV_VERSION = 65537;
    public static final int EV_SYN = 0;
    public static final int EV_KEY = 1;
    public static final int EV_REL = 2;
    public static final int EV_ABS = 3;
    public static final int EV_MSC = 4;
    public static final int EV_LED = 17;
    public static final int EV_SND = 18;
    public static final int EV_REP = 20;
    public static final int EV_FF = 21;
    public static final int EV_PWR = 22;
    public static final int EV_FF_STATUS = 23;
    public static final int EV_MAX = 31;
    public static final int KEY_RESERVED = 0;
    public static final int KEY_ESC = 1;
    public static final int KEY_1 = 2;
    public static final int KEY_2 = 3;
    public static final int KEY_3 = 4;
    public static final int KEY_4 = 5;
    public static final int KEY_5 = 6;
    public static final int KEY_6 = 7;
    public static final int KEY_7 = 8;
    public static final int KEY_8 = 9;
    public static final int KEY_9 = 10;
    public static final int KEY_0 = 11;
    public static final int KEY_MINUS = 12;
    public static final int KEY_EQUAL = 13;
    public static final int KEY_BACKSPACE = 14;
    public static final int KEY_TAB = 15;
    public static final int KEY_Q = 16;
    public static final int KEY_W = 17;
    public static final int KEY_E = 18;
    public static final int KEY_R = 19;
    public static final int KEY_T = 20;
    public static final int KEY_Y = 21;
    public static final int KEY_U = 22;
    public static final int KEY_I = 23;
    public static final int KEY_O = 24;
    public static final int KEY_P = 25;
    public static final int KEY_LEFTBRACE = 26;
    public static final int KEY_RIGHTBRACE = 27;
    public static final int KEY_ENTER = 28;
    public static final int KEY_LEFTCTRL = 29;
    public static final int KEY_A = 30;
    public static final int KEY_S = 31;
    public static final int KEY_D = 32;
    public static final int KEY_F = 33;
    public static final int KEY_G = 34;
    public static final int KEY_H = 35;
    public static final int KEY_J = 36;
    public static final int KEY_K = 37;
    public static final int KEY_L = 38;
    public static final int KEY_SEMICOLON = 39;
    public static final int KEY_APOSTROPHE = 40;
    public static final int KEY_GRAVE = 41;
    public static final int KEY_LEFTSHIFT = 42;
    public static final int KEY_BACKSLASH = 43;
    public static final int KEY_Z = 44;
    public static final int KEY_X = 45;
    public static final int KEY_C = 46;
    public static final int KEY_V = 47;
    public static final int KEY_B = 48;
    public static final int KEY_N = 49;
    public static final int KEY_M = 50;
    public static final int KEY_COMMA = 51;
    public static final int KEY_DOT = 52;
    public static final int KEY_SLASH = 53;
    public static final int KEY_RIGHTSHIFT = 54;
    public static final int KEY_KPASTERISK = 55;
    public static final int KEY_LEFTALT = 56;
    public static final int KEY_SPACE = 57;
    public static final int KEY_CAPSLOCK = 58;
    public static final int KEY_F1 = 59;
    public static final int KEY_F2 = 60;
    public static final int KEY_F3 = 61;
    public static final int KEY_F4 = 62;
    public static final int KEY_F5 = 63;
    public static final int KEY_F6 = 64;
    public static final int KEY_F7 = 65;
    public static final int KEY_F8 = 66;
    public static final int KEY_F9 = 67;
    public static final int KEY_F10 = 68;
    public static final int KEY_NUMLOCK = 69;
    public static final int KEY_SCROLLLOCK = 70;
    public static final int KEY_KP7 = 71;
    public static final int KEY_KP8 = 72;
    public static final int KEY_KP9 = 73;
    public static final int KEY_KPMINUS = 74;
    public static final int KEY_KP4 = 75;
    public static final int KEY_KP5 = 76;
    public static final int KEY_KP6 = 77;
    public static final int KEY_KPPLUS = 78;
    public static final int KEY_KP1 = 79;
    public static final int KEY_KP2 = 80;
    public static final int KEY_KP3 = 81;
    public static final int KEY_KP0 = 82;
    public static final int KEY_KPDOT = 83;
    public static final int KEY_ZENKAKUHANKAKU = 85;
    public static final int KEY_102ND = 86;
    public static final int KEY_F11 = 87;
    public static final int KEY_F12 = 88;
    public static final int KEY_RO = 89;
    public static final int KEY_KATAKANA = 90;
    public static final int KEY_HIRAGANA = 91;
    public static final int KEY_HENKAN = 92;
    public static final int KEY_KATAKANAHIRAGANA = 93;
    public static final int KEY_MUHENKAN = 94;
    public static final int KEY_KPJPCOMMA = 95;
    public static final int KEY_KPENTER = 96;
    public static final int KEY_RIGHTCTRL = 97;
    public static final int KEY_KPSLASH = 98;
    public static final int KEY_SYSRQ = 99;
    public static final int KEY_RIGHTALT = 100;
    public static final int KEY_LINEFEED = 101;
    public static final int KEY_HOME = 102;
    public static final int KEY_UP = 103;
    public static final int KEY_PAGEUP = 104;
    public static final int KEY_LEFT = 105;
    public static final int KEY_RIGHT = 106;
    public static final int KEY_END = 107;
    public static final int KEY_DOWN = 108;
    public static final int KEY_PAGEDOWN = 109;
    public static final int KEY_INSERT = 110;
    public static final int KEY_DELETE = 111;
    public static final int KEY_MACRO = 112;
    public static final int KEY_MUTE = 113;
    public static final int KEY_VOLUMEDOWN = 114;
    public static final int KEY_VOLUMEUP = 115;
    public static final int KEY_POWER = 116;
    public static final int KEY_KPEQUAL = 117;
    public static final int KEY_KPPLUSMINUS = 118;
    public static final int KEY_PAUSE = 119;
    public static final int KEY_KPCOMMA = 121;
    public static final int KEY_HANGUEL = 122;
    public static final int KEY_HANJA = 123;
    public static final int KEY_YEN = 124;
    public static final int KEY_LEFTMETA = 125;
    public static final int KEY_RIGHTMETA = 126;
    public static final int KEY_COMPOSE = 127;
    public static final int KEY_STOP = 128;
    public static final int KEY_AGAIN = 129;
    public static final int KEY_PROPS = 130;
    public static final int KEY_UNDO = 131;
    public static final int KEY_FRONT = 132;
    public static final int KEY_COPY = 133;
    public static final int KEY_OPEN = 134;
    public static final int KEY_PASTE = 135;
    public static final int KEY_FIND = 136;
    public static final int KEY_CUT = 137;
    public static final int KEY_HELP = 138;
    public static final int KEY_MENU = 139;
    public static final int KEY_CALC = 140;
    public static final int KEY_SETUP = 141;
    public static final int KEY_SLEEP = 142;
    public static final int KEY_WAKEUP = 143;
    public static final int KEY_FILE = 144;
    public static final int KEY_SENDFILE = 145;
    public static final int KEY_DELETEFILE = 146;
    public static final int KEY_XFER = 147;
    public static final int KEY_PROG1 = 148;
    public static final int KEY_PROG2 = 149;
    public static final int KEY_WWW = 150;
    public static final int KEY_MSDOS = 151;
    public static final int KEY_COFFEE = 152;
    public static final int KEY_DIRECTION = 153;
    public static final int KEY_CYCLEWINDOWS = 154;
    public static final int KEY_MAIL = 155;
    public static final int KEY_BOOKMARKS = 156;
    public static final int KEY_COMPUTER = 157;
    public static final int KEY_BACK = 158;
    public static final int KEY_FORWARD = 159;
    public static final int KEY_CLOSECD = 160;
    public static final int KEY_EJECTCD = 161;
    public static final int KEY_EJECTCLOSECD = 162;
    public static final int KEY_NEXTSONG = 163;
    public static final int KEY_PLAYPAUSE = 164;
    public static final int KEY_PREVIOUSSONG = 165;
    public static final int KEY_STOPCD = 166;
    public static final int KEY_RECORD = 167;
    public static final int KEY_REWIND = 168;
    public static final int KEY_PHONE = 169;
    public static final int KEY_ISO = 170;
    public static final int KEY_CONFIG = 171;
    public static final int KEY_HOMEPAGE = 172;
    public static final int KEY_REFRESH = 173;
    public static final int KEY_EXIT = 174;
    public static final int KEY_MOVE = 175;
    public static final int KEY_EDIT = 176;
    public static final int KEY_SCROLLUP = 177;
    public static final int KEY_SCROLLDOWN = 178;
    public static final int KEY_KPLEFTPAREN = 179;
    public static final int KEY_KPRIGHTPAREN = 180;
    public static final int KEY_F13 = 183;
    public static final int KEY_F14 = 184;
    public static final int KEY_F15 = 185;
    public static final int KEY_F16 = 186;
    public static final int KEY_F17 = 187;
    public static final int KEY_F18 = 188;
    public static final int KEY_F19 = 189;
    public static final int KEY_F20 = 190;
    public static final int KEY_F21 = 191;
    public static final int KEY_F22 = 192;
    public static final int KEY_F23 = 193;
    public static final int KEY_F24 = 194;
    public static final int KEY_PLAYCD = 200;
    public static final int KEY_PAUSECD = 201;
    public static final int KEY_PROG3 = 202;
    public static final int KEY_PROG4 = 203;
    public static final int KEY_SUSPEND = 205;
    public static final int KEY_CLOSE = 206;
    public static final int KEY_PLAY = 207;
    public static final int KEY_FASTFORWARD = 208;
    public static final int KEY_BASSBOOST = 209;
    public static final int KEY_PRINT = 210;
    public static final int KEY_HP = 211;
    public static final int KEY_CAMERA = 212;
    public static final int KEY_SOUND = 213;
    public static final int KEY_QUESTION = 214;
    public static final int KEY_EMAIL = 215;
    public static final int KEY_CHAT = 216;
    public static final int KEY_SEARCH = 217;
    public static final int KEY_CONNECT = 218;
    public static final int KEY_FINANCE = 219;
    public static final int KEY_SPORT = 220;
    public static final int KEY_SHOP = 221;
    public static final int KEY_ALTERASE = 222;
    public static final int KEY_CANCEL = 223;
    public static final int KEY_BRIGHTNESSDOWN = 224;
    public static final int KEY_BRIGHTNESSUP = 225;
    public static final int KEY_MEDIA = 226;
    public static final int KEY_SWITCHVIDEOMODE = 227;
    public static final int KEY_KBDILLUMTOGGLE = 228;
    public static final int KEY_KBDILLUMDOWN = 229;
    public static final int KEY_KBDILLUMUP = 230;
    public static final int KEY_UNKNOWN = 240;
    public static final int BTN_MISC = 256;
    public static final int BTN_0 = 256;
    public static final int BTN_1 = 257;
    public static final int BTN_2 = 258;
    public static final int BTN_3 = 259;
    public static final int BTN_4 = 260;
    public static final int BTN_5 = 261;
    public static final int BTN_6 = 262;
    public static final int BTN_7 = 263;
    public static final int BTN_8 = 264;
    public static final int BTN_9 = 265;
    public static final int BTN_MOUSE = 272;
    public static final int BTN_LEFT = 272;
    public static final int BTN_RIGHT = 273;
    public static final int BTN_MIDDLE = 274;
    public static final int BTN_SIDE = 275;
    public static final int BTN_EXTRA = 276;
    public static final int BTN_FORWARD = 277;
    public static final int BTN_BACK = 278;
    public static final int BTN_TASK = 279;
    public static final int BTN_JOYSTICK = 288;
    public static final int BTN_TRIGGER = 288;
    public static final int BTN_THUMB = 289;
    public static final int BTN_THUMB2 = 290;
    public static final int BTN_TOP = 291;
    public static final int BTN_TOP2 = 292;
    public static final int BTN_PINKIE = 293;
    public static final int BTN_BASE = 294;
    public static final int BTN_BASE2 = 295;
    public static final int BTN_BASE3 = 296;
    public static final int BTN_BASE4 = 297;
    public static final int BTN_BASE5 = 298;
    public static final int BTN_BASE6 = 299;
    public static final int BTN_DEAD = 303;
    public static final int BTN_GAMEPAD = 304;
    public static final int BTN_A = 304;
    public static final int BTN_B = 305;
    public static final int BTN_C = 306;
    public static final int BTN_X = 307;
    public static final int BTN_Y = 308;
    public static final int BTN_Z = 309;
    public static final int BTN_TL = 310;
    public static final int BTN_TR = 311;
    public static final int BTN_TL2 = 312;
    public static final int BTN_TR2 = 313;
    public static final int BTN_SELECT = 314;
    public static final int BTN_START = 315;
    public static final int BTN_MODE = 316;
    public static final int BTN_THUMBL = 317;
    public static final int BTN_THUMBR = 318;
    public static final int BTN_DIGI = 320;
    public static final int BTN_TOOL_PEN = 320;
    public static final int BTN_TOOL_RUBBER = 321;
    public static final int BTN_TOOL_BRUSH = 322;
    public static final int BTN_TOOL_PENCIL = 323;
    public static final int BTN_TOOL_AIRBRUSH = 324;
    public static final int BTN_TOOL_FINGER = 325;
    public static final int BTN_TOOL_MOUSE = 326;
    public static final int BTN_TOOL_LENS = 327;
    public static final int BTN_TOUCH = 330;
    public static final int BTN_STYLUS = 331;
    public static final int BTN_STYLUS2 = 332;
    public static final int BTN_TOOL_DOUBLETAP = 333;
    public static final int BTN_TOOL_TRIPLETAP = 334;
    public static final int BTN_WHEEL = 336;
    public static final int BTN_GEAR_DOWN = 336;
    public static final int BTN_GEAR_UP = 337;
    public static final int KEY_OK = 352;
    public static final int KEY_SELECT = 353;
    public static final int KEY_GOTO = 354;
    public static final int KEY_CLEAR = 355;
    public static final int KEY_POWER2 = 356;
    public static final int KEY_OPTION = 357;
    public static final int KEY_INFO = 358;
    public static final int KEY_TIME = 359;
    public static final int KEY_VENDOR = 360;
    public static final int KEY_ARCHIVE = 361;
    public static final int KEY_PROGRAM = 362;
    public static final int KEY_CHANNEL = 363;
    public static final int KEY_FAVORITES = 364;
    public static final int KEY_EPG = 365;
    public static final int KEY_PVR = 366;
    public static final int KEY_MHP = 367;
    public static final int KEY_LANGUAGE = 368;
    public static final int KEY_TITLE = 369;
    public static final int KEY_SUBTITLE = 370;
    public static final int KEY_ANGLE = 371;
    public static final int KEY_ZOOM = 372;
    public static final int KEY_MODE = 373;
    public static final int KEY_KEYBOARD = 374;
    public static final int KEY_SCREEN = 375;
    public static final int KEY_PC = 376;
    public static final int KEY_TV = 377;
    public static final int KEY_TV2 = 378;
    public static final int KEY_VCR = 379;
    public static final int KEY_VCR2 = 380;
    public static final int KEY_SAT = 381;
    public static final int KEY_SAT2 = 382;
    public static final int KEY_CD = 383;
    public static final int KEY_TAPE = 384;
    public static final int KEY_RADIO = 385;
    public static final int KEY_TUNER = 386;
    public static final int KEY_PLAYER = 387;
    public static final int KEY_TEXT = 388;
    public static final int KEY_DVD = 389;
    public static final int KEY_AUX = 390;
    public static final int KEY_MP3 = 391;
    public static final int KEY_AUDIO = 392;
    public static final int KEY_VIDEO = 393;
    public static final int KEY_DIRECTORY = 394;
    public static final int KEY_LIST = 395;
    public static final int KEY_MEMO = 396;
    public static final int KEY_CALENDAR = 397;
    public static final int KEY_RED = 398;
    public static final int KEY_GREEN = 399;
    public static final int KEY_YELLOW = 400;
    public static final int KEY_BLUE = 401;
    public static final int KEY_CHANNELUP = 402;
    public static final int KEY_CHANNELDOWN = 403;
    public static final int KEY_FIRST = 404;
    public static final int KEY_LAST = 405;
    public static final int KEY_AB = 406;
    public static final int KEY_NEXT = 407;
    public static final int KEY_RESTART = 408;
    public static final int KEY_SLOW = 409;
    public static final int KEY_SHUFFLE = 410;
    public static final int KEY_BREAK = 411;
    public static final int KEY_PREVIOUS = 412;
    public static final int KEY_DIGITS = 413;
    public static final int KEY_TEEN = 414;
    public static final int KEY_TWEN = 415;
    public static final int KEY_DEL_EOL = 448;
    public static final int KEY_DEL_EOS = 449;
    public static final int KEY_INS_LINE = 450;
    public static final int KEY_DEL_LINE = 451;
    public static final int KEY_FN = 464;
    public static final int KEY_FN_ESC = 465;
    public static final int KEY_FN_F1 = 466;
    public static final int KEY_FN_F2 = 467;
    public static final int KEY_FN_F3 = 468;
    public static final int KEY_FN_F4 = 469;
    public static final int KEY_FN_F5 = 470;
    public static final int KEY_FN_F6 = 471;
    public static final int KEY_FN_F7 = 472;
    public static final int KEY_FN_F8 = 473;
    public static final int KEY_FN_F9 = 474;
    public static final int KEY_FN_F10 = 475;
    public static final int KEY_FN_F11 = 476;
    public static final int KEY_FN_F12 = 477;
    public static final int KEY_FN_1 = 478;
    public static final int KEY_FN_2 = 479;
    public static final int KEY_FN_D = 480;
    public static final int KEY_FN_E = 481;
    public static final int KEY_FN_F = 482;
    public static final int KEY_FN_S = 483;
    public static final int KEY_FN_B = 484;
    public static final int KEY_MAX = 511;
    public static final int REL_X = 0;
    public static final int REL_Y = 1;
    public static final int REL_Z = 2;
    public static final int REL_RX = 3;
    public static final int REL_RY = 4;
    public static final int REL_RZ = 5;
    public static final int REL_HWHEEL = 6;
    public static final int REL_DIAL = 7;
    public static final int REL_WHEEL = 8;
    public static final int REL_MISC = 9;
    public static final int REL_MAX = 15;
    public static final int ABS_X = 0;
    public static final int ABS_Y = 1;
    public static final int ABS_Z = 2;
    public static final int ABS_RX = 3;
    public static final int ABS_RY = 4;
    public static final int ABS_RZ = 5;
    public static final int ABS_THROTTLE = 6;
    public static final int ABS_RUDDER = 7;
    public static final int ABS_WHEEL = 8;
    public static final int ABS_GAS = 9;
    public static final int ABS_BRAKE = 10;
    public static final int ABS_HAT0X = 16;
    public static final int ABS_HAT0Y = 17;
    public static final int ABS_HAT1X = 18;
    public static final int ABS_HAT1Y = 19;
    public static final int ABS_HAT2X = 20;
    public static final int ABS_HAT2Y = 21;
    public static final int ABS_HAT3X = 22;
    public static final int ABS_HAT3Y = 23;
    public static final int ABS_PRESSURE = 24;
    public static final int ABS_DISTANCE = 25;
    public static final int ABS_TILT_X = 26;
    public static final int ABS_TILT_Y = 27;
    public static final int ABS_TOOL_WIDTH = 28;
    public static final int ABS_VOLUME = 32;
    public static final int ABS_MISC = 40;
    public static final int ABS_MAX = 63;
    public static final int USAGE_MOUSE = 0;
    public static final int USAGE_JOYSTICK = 1;
    public static final int USAGE_GAMEPAD = 2;
    public static final int USAGE_KEYBOARD = 3;
    public static final int USAGE_MAX = 15;
    public static final int BUS_PCI = 1;
    public static final int BUS_ISAPNP = 2;
    public static final int BUS_USB = 3;
    public static final int BUS_HIL = 4;
    public static final int BUS_BLUETOOTH = 5;
    public static final int BUS_ISA = 16;
    public static final int BUS_I8042 = 17;
    public static final int BUS_XTKBD = 18;
    public static final int BUS_RS232 = 19;
    public static final int BUS_GAMEPORT = 20;
    public static final int BUS_PARPORT = 21;
    public static final int BUS_AMIGA = 22;
    public static final int BUS_ADB = 23;
    public static final int BUS_I2C = 24;
    public static final int BUS_HOST = 25;
    public static final int FF_STATUS_STOPPED = 0;
    public static final int FF_STATUS_PLAYING = 1;
    public static final int FF_STATUS_MAX = 1;
    public static final int FF_RUMBLE = 80;
    public static final int FF_PERIODIC = 81;
    public static final int FF_CONSTANT = 82;
    public static final int FF_SPRING = 83;
    public static final int FF_FRICTION = 84;
    public static final int FF_DAMPER = 85;
    public static final int FF_INERTIA = 86;
    public static final int FF_RAMP = 87;
    public static final int FF_SQUARE = 88;
    public static final int FF_TRIANGLE = 89;
    public static final int FF_SINE = 90;
    public static final int FF_SAW_UP = 91;
    public static final int FF_SAW_DOWN = 92;
    public static final int FF_CUSTOM = 93;
    public static final int FF_GAIN = 96;
    public static final int FF_AUTOCENTER = 97;
    public static final int FF_MAX = 127;

    NativeDefinitions() {
    }
}

/*
 * Decompiled with CFR 0.152.
 */
package net.java.games.input;

import java.io.IOException;
import net.java.games.input.AbstractController;
import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.Event;
import net.java.games.input.OSXControllers;
import net.java.games.input.OSXHIDDevice;
import net.java.games.input.OSXHIDQueue;
import net.java.games.input.Rumbler;

final class OSXAbstractController
extends AbstractController {
    private final Controller.PortType port;
    private final OSXHIDQueue queue;
    private final Controller.Type type;

    protected OSXAbstractController(OSXHIDDevice device, OSXHIDQueue queue, Component[] components, Controller[] children, Rumbler[] rumblers, Controller.Type type) {
        super(device.getProductName(), components, children, rumblers);
        this.queue = queue;
        this.type = type;
        this.port = device.getPortType();
    }

    protected final boolean getNextDeviceEvent(Event event) throws IOException {
        return OSXControllers.getNextDeviceEvent(event, this.queue);
    }

    protected final void setDeviceEventQueueSize(int size) throws IOException {
        this.queue.setQueueDepth(size);
    }

    public Controller.Type getType() {
        return this.type;
    }

    public final Controller.PortType getPortType() {
        return this.port;
    }
}

/*
 * Decompiled with CFR 0.152.
 */
package net.java.games.input;

import java.io.IOException;
import net.java.games.input.AbstractComponent;
import net.java.games.input.Component;
import net.java.games.input.OSXControllers;
import net.java.games.input.OSXHIDElement;

class OSXComponent
extends AbstractComponent {
    private final OSXHIDElement element;

    public OSXComponent(Component.Identifier id, OSXHIDElement element) {
        super(id.getName(), id);
        this.element = element;
    }

    public final boolean isRelative() {
        return this.element.isRelative();
    }

    public boolean isAnalog() {
        return this.element.isAnalog();
    }

    public final OSXHIDElement getElement() {
        return this.element;
    }

    protected float poll() throws IOException {
        return OSXControllers.poll(this.element);
    }
}

/*
 * Decompiled with CFR 0.152.
 */
package net.java.games.input;

import java.io.IOException;
import net.java.games.input.Event;
import net.java.games.input.OSXComponent;
import net.java.games.input.OSXEvent;
import net.java.games.input.OSXHIDElement;
import net.java.games.input.OSXHIDQueue;

final class OSXControllers {
    private static final OSXEvent osx_event = new OSXEvent();

    OSXControllers() {
    }

    public static final synchronized float poll(OSXHIDElement element) throws IOException {
        element.getElementValue(osx_event);
        return element.convertValue(osx_event.getValue());
    }

    public static final synchronized boolean getNextDeviceEvent(Event event, OSXHIDQueue queue) throws IOException {
        if (queue.getNextEvent(osx_event)) {
            OSXComponent component = queue.mapEvent(osx_event);
            event.set(component, component.getElement().convertValue(osx_event.getValue()), osx_event.getNanos());
            return true;
        }
        return false;
    }
}

/*
 * Decompiled with CFR 0.152.
 */
package net.java.games.input;

import java.io.File;
import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import net.java.games.input.AbstractController;
import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;
import net.java.games.input.GenericDesktopUsage;
import net.java.games.input.Keyboard;
import net.java.games.input.Mouse;
import net.java.games.input.OSXAbstractController;
import net.java.games.input.OSXComponent;
import net.java.games.input.OSXHIDDevice;
import net.java.games.input.OSXHIDDeviceIterator;
import net.java.games.input.OSXHIDElement;
import net.java.games.input.OSXHIDQueue;
import net.java.games.input.OSXKeyboard;
import net.java.games.input.OSXMouse;
import net.java.games.input.Rumbler;
import net.java.games.input.UsagePage;
import net.java.games.input.UsagePair;
import net.java.games.util.plugins.Plugin;

public final class OSXEnvironmentPlugin
extends ControllerEnvironment
implements Plugin {
    private static boolean supported = false;
    private final Controller[] controllers = this.isSupported() ? OSXEnvironmentPlugin.enumerateControllers() : new Controller[0];

    static void loadLibrary(final String lib_name) {
        AccessController.doPrivileged(new PrivilegedAction(){

            public final Object run() {
                try {
                    String lib_path = System.getProperty("net.java.games.input.librarypath");
                    if (lib_path != null) {
                        System.load(lib_path + File.separator + System.mapLibraryName(lib_name));
                    } else {
                        System.loadLibrary(lib_name);
                    }
                }
                catch (UnsatisfiedLinkError e) {
                    e.printStackTrace();
                    supported = false;
                }
                return null;
            }
        });
    }

    static String getPrivilegedProperty(final String property) {
        return (String)AccessController.doPrivileged(new PrivilegedAction(){

            public Object run() {
                return System.getProperty(property);
            }
        });
    }

    static String getPrivilegedProperty(final String property, final String default_value) {
        return (String)AccessController.doPrivileged(new PrivilegedAction(){

            public Object run() {
                return System.getProperty(property, default_value);
            }
        });
    }

    private static final boolean isMacOSXEqualsOrBetterThan(int major_required, int minor_required) {
        int minor;
        int major;
        String os_version = System.getProperty("os.version");
        StringTokenizer version_tokenizer = new StringTokenizer(os_version, ".");
        try {
            String major_str = version_tokenizer.nextToken();
            String minor_str = version_tokenizer.nextToken();
            major = Integer.parseInt(major_str);
            minor = Integer.parseInt(minor_str);
        }
        catch (Exception e) {
            OSXEnvironmentPlugin.logln("Exception occurred while trying to determine OS version: " + e);
            return false;
        }
        return major > major_required || major == major_required && minor >= minor_required;
    }

    public final Controller[] getControllers() {
        return this.controllers;
    }

    public boolean isSupported() {
        return supported;
    }

    private static final void addElements(OSXHIDQueue queue, List elements, List components, boolean map_mouse_buttons) throws IOException {
        Iterator it = elements.iterator();
        while (it.hasNext()) {
            OSXHIDElement element = (OSXHIDElement)it.next();
            Component.Identifier id = element.getIdentifier();
            if (id == null) continue;
            if (map_mouse_buttons) {
                if (id == Component.Identifier.Button._0) {
                    id = Component.Identifier.Button.LEFT;
                } else if (id == Component.Identifier.Button._1) {
                    id = Component.Identifier.Button.RIGHT;
                } else if (id == Component.Identifier.Button._2) {
                    id = Component.Identifier.Button.MIDDLE;
                }
            }
            OSXComponent component = new OSXComponent(id, element);
            components.add(component);
            queue.addElement(element, component);
        }
    }

    private static final Keyboard createKeyboardFromDevice(OSXHIDDevice device, List elements) throws IOException {
        ArrayList components = new ArrayList();
        OSXHIDQueue queue = device.createQueue(32);
        try {
            OSXEnvironmentPlugin.addElements(queue, elements, components, false);
        }
        catch (IOException e) {
            queue.release();
            throw e;
        }
        Component[] components_array = new Component[components.size()];
        components.toArray(components_array);
        OSXKeyboard keyboard = new OSXKeyboard(device, queue, components_array, new Controller[0], new Rumbler[0]);
        return keyboard;
    }

    private static final Mouse createMouseFromDevice(OSXHIDDevice device, List elements) throws IOException {
        ArrayList components = new ArrayList();
        OSXHIDQueue queue = device.createQueue(32);
        try {
            OSXEnvironmentPlugin.addElements(queue, elements, components, true);
        }
        catch (IOException e) {
            queue.release();
            throw e;
        }
        Component[] components_array = new Component[components.size()];
        components.toArray(components_array);
        OSXMouse mouse = new OSXMouse(device, queue, components_array, new Controller[0], new Rumbler[0]);
        if (mouse.getPrimaryButton() != null && mouse.getX() != null && mouse.getY() != null) {
            return mouse;
        }
        queue.release();
        return null;
    }

    private static final AbstractController createControllerFromDevice(OSXHIDDevice device, List elements, Controller.Type type) throws IOException {
        ArrayList components = new ArrayList();
        OSXHIDQueue queue = device.createQueue(32);
        try {
            OSXEnvironmentPlugin.addElements(queue, elements, components, false);
        }
        catch (IOException e) {
            queue.release();
            throw e;
        }
        Component[] components_array = new Component[components.size()];
        components.toArray(components_array);
        OSXAbstractController controller = new OSXAbstractController(device, queue, components_array, new Controller[0], new Rumbler[0], type);
        return controller;
    }

    private static final void createControllersFromDevice(OSXHIDDevice device, List controllers) throws IOException {
        AbstractController game_pad;
        UsagePair usage_pair = device.getUsagePair();
        if (usage_pair == null) {
            return;
        }
        List elements = device.getElements();
        if (usage_pair.getUsagePage() == UsagePage.GENERIC_DESKTOP && (usage_pair.getUsage() == GenericDesktopUsage.MOUSE || usage_pair.getUsage() == GenericDesktopUsage.POINTER)) {
            Mouse mouse = OSXEnvironmentPlugin.createMouseFromDevice(device, elements);
            if (mouse != null) {
                controllers.add(mouse);
            }
        } else if (usage_pair.getUsagePage() == UsagePage.GENERIC_DESKTOP && (usage_pair.getUsage() == GenericDesktopUsage.KEYBOARD || usage_pair.getUsage() == GenericDesktopUsage.KEYPAD)) {
            Keyboard keyboard = OSXEnvironmentPlugin.createKeyboardFromDevice(device, elements);
            if (keyboard != null) {
                controllers.add(keyboard);
            }
        } else if (usage_pair.getUsagePage() == UsagePage.GENERIC_DESKTOP && usage_pair.getUsage() == GenericDesktopUsage.JOYSTICK) {
            AbstractController joystick = OSXEnvironmentPlugin.createControllerFromDevice(device, elements, Controller.Type.STICK);
            if (joystick != null) {
                controllers.add(joystick);
            }
        } else if (usage_pair.getUsagePage() == UsagePage.GENERIC_DESKTOP && usage_pair.getUsage() == GenericDesktopUsage.MULTI_AXIS_CONTROLLER) {
            AbstractController multiaxis = OSXEnvironmentPlugin.createControllerFromDevice(device, elements, Controller.Type.STICK);
            if (multiaxis != null) {
                controllers.add(multiaxis);
            }
        } else if (usage_pair.getUsagePage() == UsagePage.GENERIC_DESKTOP && usage_pair.getUsage() == GenericDesktopUsage.GAME_PAD && (game_pad = OSXEnvironmentPlugin.createControllerFromDevice(device, elements, Controller.Type.GAMEPAD)) != null) {
            controllers.add(game_pad);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static final Controller[] enumerateControllers() {
        ArrayList controllers;
        block12: {
            controllers = new ArrayList();
            try {
                OSXHIDDeviceIterator it = new OSXHIDDeviceIterator();
                block9: while (true) {
                    while (true) {
                        try {
                            OSXHIDDevice device = it.next();
                            if (device == null) {
                                break block12;
                            }
                            boolean device_used = false;
                            try {
                                int old_size = controllers.size();
                                OSXEnvironmentPlugin.createControllersFromDevice(device, controllers);
                                device_used = old_size != controllers.size();
                            }
                            catch (IOException e) {
                                OSXEnvironmentPlugin.logln("Failed to create controllers from device: " + device.getProductName());
                            }
                            if (device_used) continue block9;
                            device.release();
                            continue block9;
                        }
                        catch (IOException e) {
                            OSXEnvironmentPlugin.logln("Failed to enumerate device: " + e.getMessage());
                            continue;
                        }
                        break;
                    }
                }
                finally {
                    it.close();
                }
            }
            catch (IOException e) {
                OSXEnvironmentPlugin.log("Failed to enumerate devices: " + e.getMessage());
                return new Controller[0];
            }
        }
        Controller[] controllers_array = new Controller[controllers.size()];
        controllers.toArray(controllers_array);
        return controllers_array;
    }

    static {
        String osName = OSXEnvironmentPlugin.getPrivilegedProperty("os.name", "").trim();
        if (osName.equals("Mac OS X")) {
            supported = true;
            OSXEnvironmentPlugin.loadLibrary("jinput-osx");
        }
    }
}

/*
 * Decompiled with CFR 0.152.
 */
package net.java.games.input;

class OSXEvent {
    private long type;
    private long cookie;
    private int value;
    private long nanos;

    OSXEvent() {
    }

    public void set(long type, long cookie, int value, long nanos) {
        this.type = type;
        this.cookie = cookie;
        this.value = value;
        this.nanos = nanos;
    }

    public long getType() {
        return this.type;
    }

    public long getCookie() {
        return this.cookie;
    }

    public int getValue() {
        return this.value;
    }

    public long getNanos() {
        return this.nanos;
    }
}

/*
 * Decompiled with CFR 0.152.
 */
package net.java.games.input;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import net.java.games.input.Controller;
import net.java.games.input.ElementType;
import net.java.games.input.GenericDesktopUsage;
import net.java.games.input.OSXEvent;
import net.java.games.input.OSXHIDElement;
import net.java.games.input.OSXHIDQueue;
import net.java.games.input.Usage;
import net.java.games.input.UsagePage;
import net.java.games.input.UsagePair;

final class OSXHIDDevice {
    private static final Logger log = Logger.getLogger(OSXHIDDevice.class.getName());
    private static final int AXIS_DEFAULT_MIN_VALUE = 0;
    private static final int AXIS_DEFAULT_MAX_VALUE = 65536;
    private static final String kIOHIDTransportKey = "Transport";
    private static final String kIOHIDVendorIDKey = "VendorID";
    private static final String kIOHIDVendorIDSourceKey = "VendorIDSource";
    private static final String kIOHIDProductIDKey = "ProductID";
    private static final String kIOHIDVersionNumberKey = "VersionNumber";
    private static final String kIOHIDManufacturerKey = "Manufacturer";
    private static final String kIOHIDProductKey = "Product";
    private static final String kIOHIDSerialNumberKey = "SerialNumber";
    private static final String kIOHIDCountryCodeKey = "CountryCode";
    private static final String kIOHIDLocationIDKey = "LocationID";
    private static final String kIOHIDDeviceUsageKey = "DeviceUsage";
    private static final String kIOHIDDeviceUsagePageKey = "DeviceUsagePage";
    private static final String kIOHIDDeviceUsagePairsKey = "DeviceUsagePairs";
    private static final String kIOHIDPrimaryUsageKey = "PrimaryUsage";
    private static final String kIOHIDPrimaryUsagePageKey = "PrimaryUsagePage";
    private static final String kIOHIDMaxInputReportSizeKey = "MaxInputReportSize";
    private static final String kIOHIDMaxOutputReportSizeKey = "MaxOutputReportSize";
    private static final String kIOHIDMaxFeatureReportSizeKey = "MaxFeatureReportSize";
    private static final String kIOHIDElementKey = "Elements";
    private static final String kIOHIDElementCookieKey = "ElementCookie";
    private static final String kIOHIDElementTypeKey = "Type";
    private static final String kIOHIDElementCollectionTypeKey = "CollectionType";
    private static final String kIOHIDElementUsageKey = "Usage";
    private static final String kIOHIDElementUsagePageKey = "UsagePage";
    private static final String kIOHIDElementMinKey = "Min";
    private static final String kIOHIDElementMaxKey = "Max";
    private static final String kIOHIDElementScaledMinKey = "ScaledMin";
    private static final String kIOHIDElementScaledMaxKey = "ScaledMax";
    private static final String kIOHIDElementSizeKey = "Size";
    private static final String kIOHIDElementReportSizeKey = "ReportSize";
    private static final String kIOHIDElementReportCountKey = "ReportCount";
    private static final String kIOHIDElementReportIDKey = "ReportID";
    private static final String kIOHIDElementIsArrayKey = "IsArray";
    private static final String kIOHIDElementIsRelativeKey = "IsRelative";
    private static final String kIOHIDElementIsWrappingKey = "IsWrapping";
    private static final String kIOHIDElementIsNonLinearKey = "IsNonLinear";
    private static final String kIOHIDElementHasPreferredStateKey = "HasPreferredState";
    private static final String kIOHIDElementHasNullStateKey = "HasNullState";
    private static final String kIOHIDElementUnitKey = "Unit";
    private static final String kIOHIDElementUnitExponentKey = "UnitExponent";
    private static final String kIOHIDElementNameKey = "Name";
    private static final String kIOHIDElementValueLocationKey = "ValueLocation";
    private static final String kIOHIDElementDuplicateIndexKey = "DuplicateIndex";
    private static final String kIOHIDElementParentCollectionKey = "ParentCollection";
    private final long device_address;
    private final long device_interface_address;
    private final Map properties;
    private boolean released;

    public OSXHIDDevice(long device_address, long device_interface_address) throws IOException {
        this.device_address = device_address;
        this.device_interface_address = device_interface_address;
        this.properties = this.getDeviceProperties();
        this.open();
    }

    public final Controller.PortType getPortType() {
        String transport = (String)this.properties.get(kIOHIDTransportKey);
        if (transport == null) {
            return Controller.PortType.UNKNOWN;
        }
        if (transport.equals("USB")) {
            return Controller.PortType.USB;
        }
        return Controller.PortType.UNKNOWN;
    }

    public final String getProductName() {
        return (String)this.properties.get(kIOHIDProductKey);
    }

    private final OSXHIDElement createElementFromElementProperties(Map element_properties) {
        long element_cookie = OSXHIDDevice.getLongFromProperties(element_properties, kIOHIDElementCookieKey);
        int element_type_id = OSXHIDDevice.getIntFromProperties(element_properties, kIOHIDElementTypeKey);
        ElementType element_type = ElementType.map(element_type_id);
        int min = (int)OSXHIDDevice.getLongFromProperties(element_properties, kIOHIDElementMinKey, 0L);
        int max = (int)OSXHIDDevice.getLongFromProperties(element_properties, kIOHIDElementMaxKey, 65536L);
        UsagePair device_usage_pair = this.getUsagePair();
        boolean default_relative = device_usage_pair != null && (device_usage_pair.getUsage() == GenericDesktopUsage.POINTER || device_usage_pair.getUsage() == GenericDesktopUsage.MOUSE);
        boolean is_relative = OSXHIDDevice.getBooleanFromProperties(element_properties, kIOHIDElementIsRelativeKey, default_relative);
        int usage = OSXHIDDevice.getIntFromProperties(element_properties, kIOHIDElementUsageKey);
        int usage_page = OSXHIDDevice.getIntFromProperties(element_properties, kIOHIDElementUsagePageKey);
        UsagePair usage_pair = OSXHIDDevice.createUsagePair(usage_page, usage);
        if (usage_pair == null || element_type != ElementType.INPUT_MISC && element_type != ElementType.INPUT_BUTTON && element_type != ElementType.INPUT_AXIS) {
            return null;
        }
        return new OSXHIDElement(this, usage_pair, element_cookie, element_type, min, max, is_relative);
    }

    private final void addElements(List elements, Map properties) {
        Object[] elements_properties = (Object[])properties.get(kIOHIDElementKey);
        if (elements_properties == null) {
            return;
        }
        for (int i = 0; i < elements_properties.length; ++i) {
            Map element_properties = (Map)elements_properties[i];
            OSXHIDElement element = this.createElementFromElementProperties(element_properties);
            if (element != null) {
                elements.add(element);
            }
            this.addElements(elements, element_properties);
        }
    }

    public final List getElements() {
        ArrayList elements = new ArrayList();
        this.addElements(elements, this.properties);
        return elements;
    }

    private static final long getLongFromProperties(Map properties, String key, long default_value) {
        Long long_obj = (Long)properties.get(key);
        if (long_obj == null) {
            return default_value;
        }
        return long_obj;
    }

    private static final boolean getBooleanFromProperties(Map properties, String key, boolean default_value) {
        return OSXHIDDevice.getLongFromProperties(properties, key, default_value ? 1L : 0L) != 0L;
    }

    private static final int getIntFromProperties(Map properties, String key) {
        return (int)OSXHIDDevice.getLongFromProperties(properties, key);
    }

    private static final long getLongFromProperties(Map properties, String key) {
        Long long_obj = (Long)properties.get(key);
        return long_obj;
    }

    private static final UsagePair createUsagePair(int usage_page_id, int usage_id) {
        Usage usage;
        UsagePage usage_page = UsagePage.map(usage_page_id);
        if (usage_page != null && (usage = usage_page.mapUsage(usage_id)) != null) {
            return new UsagePair(usage_page, usage);
        }
        return null;
    }

    public final UsagePair getUsagePair() {
        int usage_page_id = OSXHIDDevice.getIntFromProperties(this.properties, kIOHIDPrimaryUsagePageKey);
        int usage_id = OSXHIDDevice.getIntFromProperties(this.properties, kIOHIDPrimaryUsageKey);
        return OSXHIDDevice.createUsagePair(usage_page_id, usage_id);
    }

    private final void dumpProperties() {
        log.info(this.toString());
        OSXHIDDevice.dumpMap("", this.properties);
    }

    private static final void dumpArray(String prefix, Object[] array) {
        log.info(prefix + "{");
        for (int i = 0; i < array.length; ++i) {
            OSXHIDDevice.dumpObject(prefix + "\t", array[i]);
            log.info(prefix + ",");
        }
        log.info(prefix + "}");
    }

    private static final void dumpMap(String prefix, Map map) {
        Iterator keys = map.keySet().iterator();
        while (keys.hasNext()) {
            Object key = keys.next();
            Object value = map.get(key);
            OSXHIDDevice.dumpObject(prefix, key);
            OSXHIDDevice.dumpObject(prefix + "\t", value);
        }
    }

    private static final void dumpObject(String prefix, Object obj) {
        if (obj instanceof Long) {
            Long l = (Long)obj;
            log.info(prefix + "0x" + Long.toHexString(l));
        } else if (obj instanceof Map) {
            OSXHIDDevice.dumpMap(prefix, (Map)obj);
        } else if (obj.getClass().isArray()) {
            OSXHIDDevice.dumpArray(prefix, (Object[])obj);
        } else {
            log.info(prefix + obj);
        }
    }

    private final Map getDeviceProperties() throws IOException {
        return OSXHIDDevice.nGetDeviceProperties(this.device_address);
    }

    private static final native Map nGetDeviceProperties(long var0) throws IOException;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final synchronized void release() throws IOException {
        try {
            this.close();
        }
        finally {
            this.released = true;
            OSXHIDDevice.nReleaseDevice(this.device_address, this.device_interface_address);
        }
    }

    private static final native void nReleaseDevice(long var0, long var2);

    public final synchronized void getElementValue(long element_cookie, OSXEvent event) throws IOException {
        this.checkReleased();
        OSXHIDDevice.nGetElementValue(this.device_interface_address, element_cookie, event);
    }

    private static final native void nGetElementValue(long var0, long var2, OSXEvent var4) throws IOException;

    public final synchronized OSXHIDQueue createQueue(int queue_depth) throws IOException {
        this.checkReleased();
        long queue_address = OSXHIDDevice.nCreateQueue(this.device_interface_address);
        return new OSXHIDQueue(queue_address, queue_depth);
    }

    private static final native long nCreateQueue(long var0) throws IOException;

    private final void open() throws IOException {
        OSXHIDDevice.nOpen(this.device_interface_address);
    }

    private static final native void nOpen(long var0) throws IOException;

    private final void close() throws IOException {
        OSXHIDDevice.nClose(this.device_interface_address);
    }

    private static final native void nClose(long var0) throws IOException;

    private final void checkReleased() throws IOException {
        if (this.released) {
            throw new IOException();
        }
    }
}

/*
 * Decompiled with CFR 0.152.
 */
package net.java.games.input;

import java.io.IOException;
import net.java.games.input.OSXHIDDevice;

final class OSXHIDDeviceIterator {
    private final long iterator_address = OSXHIDDeviceIterator.nCreateIterator();

    private static final native long nCreateIterator();

    public final void close() {
        OSXHIDDeviceIterator.nReleaseIterator(this.iterator_address);
    }

    private static final native void nReleaseIterator(long var0);

    public final OSXHIDDevice next() throws IOException {
        return OSXHIDDeviceIterator.nNext(this.iterator_address);
    }

    private static final native OSXHIDDevice nNext(long var0) throws IOException;
}

/*
 * Decompiled with CFR 0.152.
 */
package net.java.games.input;

import java.io.IOException;
import net.java.games.input.ButtonUsage;
import net.java.games.input.Component;
import net.java.games.input.ElementType;
import net.java.games.input.GenericDesktopUsage;
import net.java.games.input.KeyboardUsage;
import net.java.games.input.OSXEvent;
import net.java.games.input.OSXHIDDevice;
import net.java.games.input.UsagePage;
import net.java.games.input.UsagePair;

final class OSXHIDElement {
    private final OSXHIDDevice device;
    private final UsagePair usage_pair;
    private final long element_cookie;
    private final ElementType element_type;
    private final int min;
    private final int max;
    private final Component.Identifier identifier;
    private final boolean is_relative;

    public OSXHIDElement(OSXHIDDevice device, UsagePair usage_pair, long element_cookie, ElementType element_type, int min, int max, boolean is_relative) {
        this.device = device;
        this.usage_pair = usage_pair;
        this.element_cookie = element_cookie;
        this.element_type = element_type;
        this.min = min;
        this.max = max;
        this.identifier = this.computeIdentifier();
        this.is_relative = is_relative;
    }

    private final Component.Identifier computeIdentifier() {
        if (this.usage_pair.getUsagePage() == UsagePage.GENERIC_DESKTOP) {
            return ((GenericDesktopUsage)this.usage_pair.getUsage()).getIdentifier();
        }
        if (this.usage_pair.getUsagePage() == UsagePage.BUTTON) {
            return ((ButtonUsage)this.usage_pair.getUsage()).getIdentifier();
        }
        if (this.usage_pair.getUsagePage() == UsagePage.KEYBOARD_OR_KEYPAD) {
            return ((KeyboardUsage)this.usage_pair.getUsage()).getIdentifier();
        }
        return null;
    }

    final Component.Identifier getIdentifier() {
        return this.identifier;
    }

    final long getCookie() {
        return this.element_cookie;
    }

    final ElementType getType() {
        return this.element_type;
    }

    final boolean isRelative() {
        return this.is_relative && this.identifier instanceof Component.Identifier.Axis;
    }

    final boolean isAnalog() {
        return this.identifier instanceof Component.Identifier.Axis && this.identifier != Component.Identifier.Axis.POV;
    }

    private UsagePair getUsagePair() {
        return this.usage_pair;
    }

    final void getElementValue(OSXEvent event) throws IOException {
        this.device.getElementValue(this.element_cookie, event);
    }

    final float convertValue(float value) {
        if (this.identifier == Component.Identifier.Axis.POV) {
            switch ((int)value) {
                case 0: {
                    return 0.25f;
                }
                case 1: {
                    return 0.375f;
                }
                case 2: {
                    return 0.5f;
                }
                case 3: {
                    return 0.625f;
                }
                case 4: {
                    return 0.75f;
                }
                case 5: {
                    return 0.875f;
                }
                case 6: {
                    return 1.0f;
                }
                case 7: {
                    return 0.125f;
                }
                case 8: {
                    return 0.0f;
                }
            }
            return 0.0f;
        }
        if (this.identifier instanceof Component.Identifier.Axis && !this.is_relative) {
            if (this.min == this.max) {
                return 0.0f;
            }
            if (value > (float)this.max) {
                value = this.max;
            } else if (value < (float)this.min) {
                value = this.min;
            }
            return 2.0f * (value - (float)this.min) / (float)(this.max - this.min) - 1.0f;
        }
        return value;
    }
}

/*
 * Decompiled with CFR 0.152.
 */
package net.java.games.input;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import net.java.games.input.OSXComponent;
import net.java.games.input.OSXEvent;
import net.java.games.input.OSXHIDElement;

final class OSXHIDQueue {
    private final Map map = new HashMap();
    private final long queue_address;
    private boolean released;

    public OSXHIDQueue(long address, int queue_depth) throws IOException {
        this.queue_address = address;
        try {
            this.createQueue(queue_depth);
        }
        catch (IOException e) {
            this.release();
            throw e;
        }
    }

    public final synchronized void setQueueDepth(int queue_depth) throws IOException {
        this.checkReleased();
        this.stop();
        this.close();
        this.createQueue(queue_depth);
    }

    private final void createQueue(int queue_depth) throws IOException {
        this.open(queue_depth);
        try {
            this.start();
        }
        catch (IOException e) {
            this.close();
            throw e;
        }
    }

    public final OSXComponent mapEvent(OSXEvent event) {
        return (OSXComponent)this.map.get(new Long(event.getCookie()));
    }

    private final void open(int queue_depth) throws IOException {
        OSXHIDQueue.nOpen(this.queue_address, queue_depth);
    }

    private static final native void nOpen(long var0, int var2) throws IOException;

    private final void close() throws IOException {
        OSXHIDQueue.nClose(this.queue_address);
    }

    private static final native void nClose(long var0) throws IOException;

    private final void start() throws IOException {
        OSXHIDQueue.nStart(this.queue_address);
    }

    private static final native void nStart(long var0) throws IOException;

    private final void stop() throws IOException {
        OSXHIDQueue.nStop(this.queue_address);
    }

    private static final native void nStop(long var0) throws IOException;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final synchronized void release() throws IOException {
        if (!this.released) {
            this.released = true;
            try {
                this.stop();
                this.close();
            }
            finally {
                OSXHIDQueue.nReleaseQueue(this.queue_address);
            }
        }
    }

    private static final native void nReleaseQueue(long var0) throws IOException;

    public final void addElement(OSXHIDElement element, OSXComponent component) throws IOException {
        OSXHIDQueue.nAddElement(this.queue_address, element.getCookie());
        this.map.put(new Long(element.getCookie()), component);
    }

    private static final native void nAddElement(long var0, long var2) throws IOException;

    public final void removeElement(OSXHIDElement element) throws IOException {
        OSXHIDQueue.nRemoveElement(this.queue_address, element.getCookie());
        this.map.remove(new Long(element.getCookie()));
    }

    private static final native void nRemoveElement(long var0, long var2) throws IOException;

    public final synchronized boolean getNextEvent(OSXEvent event) throws IOException {
        this.checkReleased();
        return OSXHIDQueue.nGetNextEvent(this.queue_address, event);
    }

    private static final native boolean nGetNextEvent(long var0, OSXEvent var2) throws IOException;

    private final void checkReleased() throws IOException {
        if (this.released) {
            throw new IOException("Queue is released");
        }
    }
}

/*
 * Decompiled with CFR 0.152.
 */
package net.java.games.input;

import java.io.IOException;
import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.Event;
import net.java.games.input.Keyboard;
import net.java.games.input.OSXControllers;
import net.java.games.input.OSXHIDDevice;
import net.java.games.input.OSXHIDQueue;
import net.java.games.input.Rumbler;

final class OSXKeyboard
extends Keyboard {
    private final Controller.PortType port;
    private final OSXHIDQueue queue;

    protected OSXKeyboard(OSXHIDDevice device, OSXHIDQueue queue, Component[] components, Controller[] children, Rumbler[] rumblers) {
        super(device.getProductName(), components, children, rumblers);
        this.queue = queue;
        this.port = device.getPortType();
    }

    protected final boolean getNextDeviceEvent(Event event) throws IOException {
        return OSXControllers.getNextDeviceEvent(event, this.queue);
    }

    protected final void setDeviceEventQueueSize(int size) throws IOException {
        this.queue.setQueueDepth(size);
    }

    public final Controller.PortType getPortType() {
        return this.port;
    }
}

/*
 * Decompiled with CFR 0.152.
 */
package net.java.games.input;

import java.io.IOException;
import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.Event;
import net.java.games.input.Mouse;
import net.java.games.input.OSXControllers;
import net.java.games.input.OSXHIDDevice;
import net.java.games.input.OSXHIDQueue;
import net.java.games.input.Rumbler;

final class OSXMouse
extends Mouse {
    private final Controller.PortType port;
    private final OSXHIDQueue queue;

    protected OSXMouse(OSXHIDDevice device, OSXHIDQueue queue, Component[] components, Controller[] children, Rumbler[] rumblers) {
        super(device.getProductName(), components, children, rumblers);
        this.queue = queue;
        this.port = device.getPortType();
    }

    protected final boolean getNextDeviceEvent(Event event) throws IOException {
        return OSXControllers.getNextDeviceEvent(event, this.queue);
    }

    protected final void setDeviceEventQueueSize(int size) throws IOException {
        this.queue.setQueueDepth(size);
    }

    public final Controller.PortType getPortType() {
        return this.port;
    }
}

/*
 * Decompiled with CFR 0.152.
 */
package net.java.games.input;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.StringTokenizer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import net.java.games.input.DefaultControllerEnvironment;

class PluginClassLoader
extends ClassLoader {
    private static String pluginDirectory;
    private static final FileFilter JAR_FILTER;
    static final /* synthetic */ boolean $assertionsDisabled;

    public PluginClassLoader() {
        super(Thread.currentThread().getContextClassLoader());
    }

    protected Class findClass(String name) throws ClassNotFoundException {
        byte[] b = this.loadClassData(name);
        return this.defineClass(name, b, 0, b.length);
    }

    private byte[] loadClassData(String name) throws ClassNotFoundException {
        if (pluginDirectory == null) {
            pluginDirectory = DefaultControllerEnvironment.libPath + File.separator + "controller";
        }
        try {
            return this.loadClassFromDirectory(name);
        }
        catch (Exception e) {
            try {
                return this.loadClassFromJAR(name);
            }
            catch (IOException e2) {
                throw new ClassNotFoundException(name, e2);
            }
        }
    }

    private byte[] loadClassFromDirectory(String name) throws ClassNotFoundException, IOException {
        StringTokenizer tokenizer = new StringTokenizer(name, ".");
        StringBuffer path = new StringBuffer(pluginDirectory);
        while (tokenizer.hasMoreTokens()) {
            path.append(File.separator);
            path.append(tokenizer.nextToken());
        }
        path.append(".class");
        File file = new File(path.toString());
        if (!file.exists()) {
            throw new ClassNotFoundException(name);
        }
        FileInputStream fileInputStream = new FileInputStream(file);
        if (!$assertionsDisabled && file.length() > Integer.MAX_VALUE) {
            throw new AssertionError();
        }
        int length = (int)file.length();
        byte[] bytes = new byte[length];
        int length2 = fileInputStream.read(bytes);
        if (!$assertionsDisabled && length != length2) {
            throw new AssertionError();
        }
        return bytes;
    }

    private byte[] loadClassFromJAR(String name) throws ClassNotFoundException, IOException {
        File dir = new File(pluginDirectory);
        File[] jarFiles = dir.listFiles(JAR_FILTER);
        if (jarFiles == null) {
            throw new ClassNotFoundException("Could not find class " + name);
        }
        for (int i = 0; i < jarFiles.length; ++i) {
            JarFile jarfile = new JarFile(jarFiles[i]);
            JarEntry jarentry = jarfile.getJarEntry(name + ".class");
            if (jarentry == null) continue;
            InputStream jarInputStream = jarfile.getInputStream(jarentry);
            if (!$assertionsDisabled && jarentry.getSize() > Integer.MAX_VALUE) {
                throw new AssertionError();
            }
            int length = (int)jarentry.getSize();
            if (!$assertionsDisabled && length < 0) {
                throw new AssertionError();
            }
            byte[] bytes = new byte[length];
            int length2 = jarInputStream.read(bytes);
            if (!$assertionsDisabled && length != length2) {
                throw new AssertionError();
            }
            return bytes;
        }
        throw new FileNotFoundException(name);
    }

    static {
        $assertionsDisabled = !PluginClassLoader.class.desiredAssertionStatus();
        JAR_FILTER = new JarFileFilter();
    }

    private static class JarFileFilter
    implements FileFilter {
        private JarFileFilter() {
        }

        public boolean accept(File file) {
            return file.getName().toUpperCase().endsWith(".JAR");
        }
    }
}

/*
 * Decompiled with CFR 0.152.
 */
package net.java.games.input;

import java.io.IOException;
import net.java.games.input.DataQueue;
import net.java.games.input.RawDeviceInfo;
import net.java.games.input.RawInputEventQueue;
import net.java.games.input.RawKeyboardEvent;
import net.java.games.input.RawMouseEvent;

final class RawDevice {
    public static final int RI_MOUSE_LEFT_BUTTON_DOWN = 1;
    public static final int RI_MOUSE_LEFT_BUTTON_UP = 2;
    public static final int RI_MOUSE_RIGHT_BUTTON_DOWN = 4;
    public static final int RI_MOUSE_RIGHT_BUTTON_UP = 8;
    public static final int RI_MOUSE_MIDDLE_BUTTON_DOWN = 16;
    public static final int RI_MOUSE_MIDDLE_BUTTON_UP = 32;
    public static final int RI_MOUSE_BUTTON_1_DOWN = 1;
    public static final int RI_MOUSE_BUTTON_1_UP = 2;
    public static final int RI_MOUSE_BUTTON_2_DOWN = 4;
    public static final int RI_MOUSE_BUTTON_2_UP = 8;
    public static final int RI_MOUSE_BUTTON_3_DOWN = 16;
    public static final int RI_MOUSE_BUTTON_3_UP = 32;
    public static final int RI_MOUSE_BUTTON_4_DOWN = 64;
    public static final int RI_MOUSE_BUTTON_4_UP = 128;
    public static final int RI_MOUSE_BUTTON_5_DOWN = 256;
    public static final int RI_MOUSE_BUTTON_5_UP = 512;
    public static final int RI_MOUSE_WHEEL = 1024;
    public static final int MOUSE_MOVE_RELATIVE = 0;
    public static final int MOUSE_MOVE_ABSOLUTE = 1;
    public static final int MOUSE_VIRTUAL_DESKTOP = 2;
    public static final int MOUSE_ATTRIBUTES_CHANGED = 4;
    public static final int RIM_TYPEHID = 2;
    public static final int RIM_TYPEKEYBOARD = 1;
    public static final int RIM_TYPEMOUSE = 0;
    public static final int WM_KEYDOWN = 256;
    public static final int WM_KEYUP = 257;
    public static final int WM_SYSKEYDOWN = 260;
    public static final int WM_SYSKEYUP = 261;
    private final RawInputEventQueue queue;
    private final long handle;
    private final int type;
    private DataQueue keyboard_events;
    private DataQueue mouse_events;
    private DataQueue processed_keyboard_events;
    private DataQueue processed_mouse_events;
    private final boolean[] button_states = new boolean[5];
    private int wheel;
    private int relative_x;
    private int relative_y;
    private int last_x;
    private int last_y;
    private int event_relative_x;
    private int event_relative_y;
    private int event_last_x;
    private int event_last_y;
    private final boolean[] key_states = new boolean[255];

    public RawDevice(RawInputEventQueue queue, long handle, int type) {
        this.queue = queue;
        this.handle = handle;
        this.type = type;
        this.setBufferSize(32);
    }

    public final synchronized void addMouseEvent(long millis, int flags, int button_flags, int button_data, long raw_buttons, long last_x, long last_y, long extra_information) {
        if (this.mouse_events.hasRemaining()) {
            RawMouseEvent event = (RawMouseEvent)this.mouse_events.get();
            event.set(millis, flags, button_flags, button_data, raw_buttons, last_x, last_y, extra_information);
        }
    }

    public final synchronized void addKeyboardEvent(long millis, int make_code, int flags, int vkey, int message, long extra_information) {
        if (this.keyboard_events.hasRemaining()) {
            RawKeyboardEvent event = (RawKeyboardEvent)this.keyboard_events.get();
            event.set(millis, make_code, flags, vkey, message, extra_information);
        }
    }

    public final synchronized void pollMouse() {
        this.wheel = 0;
        this.relative_y = 0;
        this.relative_x = 0;
        this.mouse_events.flip();
        while (this.mouse_events.hasRemaining()) {
            RawMouseEvent event = (RawMouseEvent)this.mouse_events.get();
            boolean has_update = this.processMouseEvent(event);
            if (!has_update || !this.processed_mouse_events.hasRemaining()) continue;
            RawMouseEvent processed_event = (RawMouseEvent)this.processed_mouse_events.get();
            processed_event.set(event);
        }
        this.mouse_events.compact();
    }

    public final synchronized void pollKeyboard() {
        this.keyboard_events.flip();
        while (this.keyboard_events.hasRemaining()) {
            RawKeyboardEvent event = (RawKeyboardEvent)this.keyboard_events.get();
            boolean has_update = this.processKeyboardEvent(event);
            if (!has_update || !this.processed_keyboard_events.hasRemaining()) continue;
            RawKeyboardEvent processed_event = (RawKeyboardEvent)this.processed_keyboard_events.get();
            processed_event.set(event);
        }
        this.keyboard_events.compact();
    }

    private final boolean updateButtonState(int button_id, int button_flags, int down_flag, int up_flag) {
        if (button_id >= this.button_states.length) {
            return false;
        }
        if ((button_flags & down_flag) != 0) {
            this.button_states[button_id] = true;
            return true;
        }
        if ((button_flags & up_flag) != 0) {
            this.button_states[button_id] = false;
            return true;
        }
        return false;
    }

    private final boolean processKeyboardEvent(RawKeyboardEvent event) {
        int message = event.getMessage();
        int vkey = event.getVKey();
        if (vkey >= this.key_states.length) {
            return false;
        }
        if (message == 256 || message == 260) {
            this.key_states[vkey] = true;
            return true;
        }
        if (message == 257 || message == 261) {
            this.key_states[vkey] = false;
            return true;
        }
        return false;
    }

    public final boolean isKeyDown(int vkey) {
        return this.key_states[vkey];
    }

    private final boolean processMouseEvent(RawMouseEvent event) {
        int dy;
        int dx;
        boolean has_update = false;
        int button_flags = event.getButtonFlags();
        has_update = this.updateButtonState(0, button_flags, 1, 2) || has_update;
        has_update = this.updateButtonState(1, button_flags, 4, 8) || has_update;
        has_update = this.updateButtonState(2, button_flags, 16, 32) || has_update;
        has_update = this.updateButtonState(3, button_flags, 64, 128) || has_update;
        boolean bl = has_update = this.updateButtonState(4, button_flags, 256, 512) || has_update;
        if ((event.getFlags() & 1) != 0) {
            dx = event.getLastX() - this.last_x;
            dy = event.getLastY() - this.last_y;
            this.last_x = event.getLastX();
            this.last_y = event.getLastY();
        } else {
            dx = event.getLastX();
            dy = event.getLastY();
        }
        int dwheel = 0;
        if ((button_flags & 0x400) != 0) {
            dwheel = event.getWheelDelta();
        }
        this.relative_x += dx;
        this.relative_y += dy;
        this.wheel += dwheel;
        has_update = dx != 0 || dy != 0 || dwheel != 0 || has_update;
        return has_update;
    }

    public final int getWheel() {
        return this.wheel;
    }

    public final int getEventRelativeX() {
        return this.event_relative_x;
    }

    public final int getEventRelativeY() {
        return this.event_relative_y;
    }

    public final int getRelativeX() {
        return this.relative_x;
    }

    public final int getRelativeY() {
        return this.relative_y;
    }

    public final synchronized boolean getNextKeyboardEvent(RawKeyboardEvent event) {
        this.processed_keyboard_events.flip();
        if (!this.processed_keyboard_events.hasRemaining()) {
            this.processed_keyboard_events.compact();
            return false;
        }
        RawKeyboardEvent next_event = (RawKeyboardEvent)this.processed_keyboard_events.get();
        event.set(next_event);
        this.processed_keyboard_events.compact();
        return true;
    }

    public final synchronized boolean getNextMouseEvent(RawMouseEvent event) {
        this.processed_mouse_events.flip();
        if (!this.processed_mouse_events.hasRemaining()) {
            this.processed_mouse_events.compact();
            return false;
        }
        RawMouseEvent next_event = (RawMouseEvent)this.processed_mouse_events.get();
        if ((next_event.getFlags() & 1) != 0) {
            this.event_relative_x = next_event.getLastX() - this.event_last_x;
            this.event_relative_y = next_event.getLastY() - this.event_last_y;
            this.event_last_x = next_event.getLastX();
            this.event_last_y = next_event.getLastY();
        } else {
            this.event_relative_x = next_event.getLastX();
            this.event_relative_y = next_event.getLastY();
        }
        event.set(next_event);
        this.processed_mouse_events.compact();
        return true;
    }

    public final boolean getButtonState(int button_id) {
        if (button_id >= this.button_states.length) {
            return false;
        }
        return this.button_states[button_id];
    }

    public final void setBufferSize(int size) {
        this.keyboard_events = new DataQueue(size, RawKeyboardEvent.class);
        this.mouse_events = new DataQueue(size, RawMouseEvent.class);
        this.processed_keyboard_events = new DataQueue(size, RawKeyboardEvent.class);
        this.processed_mouse_events = new DataQueue(size, RawMouseEvent.class);
    }

    public final int getType() {
        return this.type;
    }

    public final long getHandle() {
        return this.handle;
    }

    public final String getName() throws IOException {
        return RawDevice.nGetName(this.handle);
    }

    private static final native String nGetName(long var0) throws IOException;

    public final RawDeviceInfo getInfo() throws IOException {
        return RawDevice.nGetInfo(this, this.handle);
    }

    private static final native RawDeviceInfo nGetInfo(RawDevice var0, long var1) throws IOException;
}

/*
 * Decompiled with CFR 0.152.
 */
package net.java.games.input;

import java.io.IOException;
import net.java.games.input.Controller;
import net.java.games.input.RawDevice;
import net.java.games.input.SetupAPIDevice;

abstract class RawDeviceInfo {
    RawDeviceInfo() {
    }

    public abstract Controller createControllerFromDevice(RawDevice var1, SetupAPIDevice var2) throws IOException;

    public abstract int getUsage();

    public abstract int getUsagePage();

    public abstract long getHandle();

    public final boolean equals(Object other) {
        if (!(other instanceof RawDeviceInfo)) {
            return false;
        }
        RawDeviceInfo other_info = (RawDeviceInfo)other;
        return other_info.getUsage() == this.getUsage() && other_info.getUsagePage() == this.getUsagePage();
    }

    public final int hashCode() {
        return this.getUsage() ^ this.getUsagePage();
    }
}

/*
 * Decompiled with CFR 0.152.
 */
package net.java.games.input;

import java.io.IOException;
import net.java.games.input.Controller;
import net.java.games.input.RawDevice;
import net.java.games.input.RawDeviceInfo;
import net.java.games.input.SetupAPIDevice;

class RawHIDInfo
extends RawDeviceInfo {
    private final RawDevice device;
    private final int vendor_id;
    private final int product_id;
    private final int version;
    private final int page;
    private final int usage;

    public RawHIDInfo(RawDevice device, int vendor_id, int product_id, int version, int page, int usage) {
        this.device = device;
        this.vendor_id = vendor_id;
        this.product_id = product_id;
        this.version = version;
        this.page = page;
        this.usage = usage;
    }

    public final int getUsage() {
        return this.usage;
    }

    public final int getUsagePage() {
        return this.page;
    }

    public final long getHandle() {
        return this.device.getHandle();
    }

    public final Controller createControllerFromDevice(RawDevice device, SetupAPIDevice setupapi_device) throws IOException {
        return null;
    }
}

/*
 * Decompiled with CFR 0.152.
 */
package net.java.games.input;

import net.java.games.input.Component;

final class RawIdentifierMap {
    public static final int VK_LBUTTON = 1;
    public static final int VK_RBUTTON = 2;
    public static final int VK_CANCEL = 3;
    public static final int VK_MBUTTON = 4;
    public static final int VK_XBUTTON1 = 5;
    public static final int VK_XBUTTON2 = 6;
    public static final int VK_BACK = 8;
    public static final int VK_TAB = 9;
    public static final int VK_CLEAR = 12;
    public static final int VK_RETURN = 13;
    public static final int VK_SHIFT = 16;
    public static final int VK_CONTROL = 17;
    public static final int VK_MENU = 18;
    public static final int VK_PAUSE = 19;
    public static final int VK_CAPITAL = 20;
    public static final int VK_KANA = 21;
    public static final int VK_HANGEUL = 21;
    public static final int VK_HANGUL = 21;
    public static final int VK_JUNJA = 23;
    public static final int VK_FINAL = 24;
    public static final int VK_HANJA = 25;
    public static final int VK_KANJI = 25;
    public static final int VK_ESCAPE = 27;
    public static final int VK_CONVERT = 28;
    public static final int VK_NONCONVERT = 29;
    public static final int VK_ACCEPT = 30;
    public static final int VK_MODECHANGE = 31;
    public static final int VK_SPACE = 32;
    public static final int VK_PRIOR = 33;
    public static final int VK_NEXT = 34;
    public static final int VK_END = 35;
    public static final int VK_HOME = 36;
    public static final int VK_LEFT = 37;
    public static final int VK_UP = 38;
    public static final int VK_RIGHT = 39;
    public static final int VK_DOWN = 40;
    public static final int VK_SELECT = 41;
    public static final int VK_PRINT = 42;
    public static final int VK_EXECUTE = 43;
    public static final int VK_SNAPSHOT = 44;
    public static final int VK_INSERT = 45;
    public static final int VK_DELETE = 46;
    public static final int VK_HELP = 47;
    public static final int VK_0 = 48;
    public static final int VK_1 = 49;
    public static final int VK_2 = 50;
    public static final int VK_3 = 51;
    public static final int VK_4 = 52;
    public static final int VK_5 = 53;
    public static final int VK_6 = 54;
    public static final int VK_7 = 55;
    public static final int VK_8 = 56;
    public static final int VK_9 = 57;
    public static final int VK_A = 65;
    public static final int VK_B = 66;
    public static final int VK_C = 67;
    public static final int VK_D = 68;
    public static final int VK_E = 69;
    public static final int VK_F = 70;
    public static final int VK_G = 71;
    public static final int VK_H = 72;
    public static final int VK_I = 73;
    public static final int VK_J = 74;
    public static final int VK_K = 75;
    public static final int VK_L = 76;
    public static final int VK_M = 77;
    public static final int VK_N = 78;
    public static final int VK_O = 79;
    public static final int VK_P = 80;
    public static final int VK_Q = 81;
    public static final int VK_R = 82;
    public static final int VK_S = 83;
    public static final int VK_T = 84;
    public static final int VK_U = 85;
    public static final int VK_V = 86;
    public static final int VK_W = 87;
    public static final int VK_X = 88;
    public static final int VK_Y = 89;
    public static final int VK_Z = 90;
    public static final int VK_LWIN = 91;
    public static final int VK_RWIN = 92;
    public static final int VK_APPS = 93;
    public static final int VK_SLEEP = 95;
    public static final int VK_NUMPAD0 = 96;
    public static final int VK_NUMPAD1 = 97;
    public static final int VK_NUMPAD2 = 98;
    public static final int VK_NUMPAD3 = 99;
    public static final int VK_NUMPAD4 = 100;
    public static final int VK_NUMPAD5 = 101;
    public static final int VK_NUMPAD6 = 102;
    public static final int VK_NUMPAD7 = 103;
    public static final int VK_NUMPAD8 = 104;
    public static final int VK_NUMPAD9 = 105;
    public static final int VK_MULTIPLY = 106;
    public static final int VK_ADD = 107;
    public static final int VK_SEPARATOR = 108;
    public static final int VK_SUBTRACT = 109;
    public static final int VK_DECIMAL = 110;
    public static final int VK_DIVIDE = 111;
    public static final int VK_F1 = 112;
    public static final int VK_F2 = 113;
    public static final int VK_F3 = 114;
    public static final int VK_F4 = 115;
    public static final int VK_F5 = 116;
    public static final int VK_F6 = 117;
    public static final int VK_F7 = 118;
    public static final int VK_F8 = 119;
    public static final int VK_F9 = 120;
    public static final int VK_F10 = 121;
    public static final int VK_F11 = 122;
    public static final int VK_F12 = 123;
    public static final int VK_F13 = 124;
    public static final int VK_F14 = 125;
    public static final int VK_F15 = 126;
    public static final int VK_F16 = 127;
    public static final int VK_F17 = 128;
    public static final int VK_F18 = 129;
    public static final int VK_F19 = 130;
    public static final int VK_F20 = 131;
    public static final int VK_F21 = 132;
    public static final int VK_F22 = 133;
    public static final int VK_F23 = 134;
    public static final int VK_F24 = 135;
    public static final int VK_NUMLOCK = 144;
    public static final int VK_SCROLL = 145;
    public static final int VK_OEM_NEC_EQUAL = 146;
    public static final int VK_OEM_FJ_JISHO = 146;
    public static final int VK_OEM_FJ_MASSHOU = 147;
    public static final int VK_OEM_FJ_TOUROKU = 148;
    public static final int VK_OEM_FJ_LOYA = 149;
    public static final int VK_OEM_FJ_ROYA = 150;
    public static final int VK_LSHIFT = 160;
    public static final int VK_RSHIFT = 161;
    public static final int VK_LCONTROL = 162;
    public static final int VK_RCONTROL = 163;
    public static final int VK_LMENU = 164;
    public static final int VK_RMENU = 165;
    public static final int VK_BROWSER_BACK = 166;
    public static final int VK_BROWSER_FORWARD = 167;
    public static final int VK_BROWSER_REFRESH = 168;
    public static final int VK_BROWSER_STOP = 169;
    public static final int VK_BROWSER_SEARCH = 170;
    public static final int VK_BROWSER_FAVORITES = 171;
    public static final int VK_BROWSER_HOME = 172;
    public static final int VK_VOLUME_MUTE = 173;
    public static final int VK_VOLUME_DOWN = 174;
    public static final int VK_VOLUME_UP = 175;
    public static final int VK_MEDIA_NEXT_TRACK = 176;
    public static final int VK_MEDIA_PREV_TRACK = 177;
    public static final int VK_MEDIA_STOP = 178;
    public static final int VK_MEDIA_PLAY_PAUSE = 179;
    public static final int VK_LAUNCH_MAIL = 180;
    public static final int VK_LAUNCH_MEDIA_SELECT = 181;
    public static final int VK_LAUNCH_APP1 = 182;
    public static final int VK_LAUNCH_APP2 = 183;
    public static final int VK_OEM_1 = 186;
    public static final int VK_OEM_PLUS = 187;
    public static final int VK_OEM_COMMA = 188;
    public static final int VK_OEM_MINUS = 189;
    public static final int VK_OEM_PERIOD = 190;
    public static final int VK_OEM_2 = 191;
    public static final int VK_OEM_3 = 192;
    public static final int VK_OEM_4 = 219;
    public static final int VK_OEM_5 = 220;
    public static final int VK_OEM_6 = 221;
    public static final int VK_OEM_7 = 222;
    public static final int VK_OEM_8 = 223;
    public static final int VK_OEM_AX = 225;
    public static final int VK_OEM_102 = 226;
    public static final int VK_ICO_HELP = 227;
    public static final int VK_ICO_00 = 228;
    public static final int VK_PROCESSKEY = 229;
    public static final int VK_ICO_CLEAR = 230;
    public static final int VK_PACKET = 231;
    public static final int VK_OEM_RESET = 233;
    public static final int VK_OEM_JUMP = 234;
    public static final int VK_OEM_PA1 = 235;
    public static final int VK_OEM_PA2 = 236;
    public static final int VK_OEM_PA3 = 237;
    public static final int VK_OEM_WSCTRL = 238;
    public static final int VK_OEM_CUSEL = 239;
    public static final int VK_OEM_ATTN = 240;
    public static final int VK_OEM_FINISH = 241;
    public static final int VK_OEM_COPY = 242;
    public static final int VK_OEM_AUTO = 243;
    public static final int VK_OEM_ENLW = 244;
    public static final int VK_OEM_BACKTAB = 245;
    public static final int VK_ATTN = 246;
    public static final int VK_CRSEL = 247;
    public static final int VK_EXSEL = 248;
    public static final int VK_EREOF = 249;
    public static final int VK_PLAY = 250;
    public static final int VK_ZOOM = 251;
    public static final int VK_NONAME = 252;
    public static final int VK_PA1 = 253;
    public static final int VK_OEM_CLEAR = 254;

    RawIdentifierMap() {
    }

    public static final Component.Identifier.Key mapVKey(int vkey) {
        switch (vkey) {
            case 27: {
                return Component.Identifier.Key.ESCAPE;
            }
            case 49: {
                return Component.Identifier.Key._1;
            }
            case 50: {
                return Component.Identifier.Key._2;
            }
            case 51: {
                return Component.Identifier.Key._3;
            }
            case 52: {
                return Component.Identifier.Key._4;
            }
            case 53: {
                return Component.Identifier.Key._5;
            }
            case 54: {
                return Component.Identifier.Key._6;
            }
            case 55: {
                return Component.Identifier.Key._7;
            }
            case 56: {
                return Component.Identifier.Key._8;
            }
            case 57: {
                return Component.Identifier.Key._9;
            }
            case 48: {
                return Component.Identifier.Key._0;
            }
            case 146: {
                return Component.Identifier.Key.NUMPADEQUAL;
            }
            case 8: {
                return Component.Identifier.Key.BACK;
            }
            case 9: {
                return Component.Identifier.Key.TAB;
            }
            case 81: {
                return Component.Identifier.Key.Q;
            }
            case 87: {
                return Component.Identifier.Key.W;
            }
            case 69: {
                return Component.Identifier.Key.E;
            }
            case 82: {
                return Component.Identifier.Key.R;
            }
            case 84: {
                return Component.Identifier.Key.T;
            }
            case 89: {
                return Component.Identifier.Key.Y;
            }
            case 85: {
                return Component.Identifier.Key.U;
            }
            case 73: {
                return Component.Identifier.Key.I;
            }
            case 79: {
                return Component.Identifier.Key.O;
            }
            case 80: {
                return Component.Identifier.Key.P;
            }
            case 219: {
                return Component.Identifier.Key.LBRACKET;
            }
            case 221: {
                return Component.Identifier.Key.RBRACKET;
            }
            case 13: {
                return Component.Identifier.Key.RETURN;
            }
            case 17:
            case 162: {
                return Component.Identifier.Key.LCONTROL;
            }
            case 65: {
                return Component.Identifier.Key.A;
            }
            case 83: {
                return Component.Identifier.Key.S;
            }
            case 68: {
                return Component.Identifier.Key.D;
            }
            case 70: {
                return Component.Identifier.Key.F;
            }
            case 71: {
                return Component.Identifier.Key.G;
            }
            case 72: {
                return Component.Identifier.Key.H;
            }
            case 74: {
                return Component.Identifier.Key.J;
            }
            case 75: {
                return Component.Identifier.Key.K;
            }
            case 76: {
                return Component.Identifier.Key.L;
            }
            case 192: {
                return Component.Identifier.Key.GRAVE;
            }
            case 16:
            case 160: {
                return Component.Identifier.Key.LSHIFT;
            }
            case 90: {
                return Component.Identifier.Key.Z;
            }
            case 88: {
                return Component.Identifier.Key.X;
            }
            case 67: {
                return Component.Identifier.Key.C;
            }
            case 86: {
                return Component.Identifier.Key.V;
            }
            case 66: {
                return Component.Identifier.Key.B;
            }
            case 78: {
                return Component.Identifier.Key.N;
            }
            case 77: {
                return Component.Identifier.Key.M;
            }
            case 188: {
                return Component.Identifier.Key.COMMA;
            }
            case 190: {
                return Component.Identifier.Key.PERIOD;
            }
            case 161: {
                return Component.Identifier.Key.RSHIFT;
            }
            case 106: {
                return Component.Identifier.Key.MULTIPLY;
            }
            case 18:
            case 164: {
                return Component.Identifier.Key.LALT;
            }
            case 32: {
                return Component.Identifier.Key.SPACE;
            }
            case 20: {
                return Component.Identifier.Key.CAPITAL;
            }
            case 112: {
                return Component.Identifier.Key.F1;
            }
            case 113: {
                return Component.Identifier.Key.F2;
            }
            case 114: {
                return Component.Identifier.Key.F3;
            }
            case 115: {
                return Component.Identifier.Key.F4;
            }
            case 116: {
                return Component.Identifier.Key.F5;
            }
            case 117: {
                return Component.Identifier.Key.F6;
            }
            case 118: {
                return Component.Identifier.Key.F7;
            }
            case 119: {
                return Component.Identifier.Key.F8;
            }
            case 120: {
                return Component.Identifier.Key.F9;
            }
            case 121: {
                return Component.Identifier.Key.F10;
            }
            case 144: {
                return Component.Identifier.Key.NUMLOCK;
            }
            case 145: {
                return Component.Identifier.Key.SCROLL;
            }
            case 103: {
                return Component.Identifier.Key.NUMPAD7;
            }
            case 104: {
                return Component.Identifier.Key.NUMPAD8;
            }
            case 105: {
                return Component.Identifier.Key.NUMPAD9;
            }
            case 109: {
                return Component.Identifier.Key.SUBTRACT;
            }
            case 100: {
                return Component.Identifier.Key.NUMPAD4;
            }
            case 101: {
                return Component.Identifier.Key.NUMPAD5;
            }
            case 102: {
                return Component.Identifier.Key.NUMPAD6;
            }
            case 107: {
                return Component.Identifier.Key.ADD;
            }
            case 97: {
                return Component.Identifier.Key.NUMPAD1;
            }
            case 98: {
                return Component.Identifier.Key.NUMPAD2;
            }
            case 99: {
                return Component.Identifier.Key.NUMPAD3;
            }
            case 96: {
                return Component.Identifier.Key.NUMPAD0;
            }
            case 110: {
                return Component.Identifier.Key.DECIMAL;
            }
            case 122: {
                return Component.Identifier.Key.F11;
            }
            case 123: {
                return Component.Identifier.Key.F12;
            }
            case 124: {
                return Component.Identifier.Key.F13;
            }
            case 125: {
                return Component.Identifier.Key.F14;
            }
            case 126: {
                return Component.Identifier.Key.F15;
            }
            case 21: {
                return Component.Identifier.Key.KANA;
            }
            case 28: {
                return Component.Identifier.Key.CONVERT;
            }
            case 25: {
                return Component.Identifier.Key.KANJI;
            }
            case 225: {
                return Component.Identifier.Key.AX;
            }
            case 163: {
                return Component.Identifier.Key.RCONTROL;
            }
            case 108: {
                return Component.Identifier.Key.NUMPADCOMMA;
            }
            case 111: {
                return Component.Identifier.Key.DIVIDE;
            }
            case 44: {
                return Component.Identifier.Key.SYSRQ;
            }
            case 165: {
                return Component.Identifier.Key.RALT;
            }
            case 19: {
                return Component.Identifier.Key.PAUSE;
            }
            case 36: {
                return Component.Identifier.Key.HOME;
            }
            case 38: {
                return Component.Identifier.Key.UP;
            }
            case 33: {
                return Component.Identifier.Key.PAGEUP;
            }
            case 37: {
                return Component.Identifier.Key.LEFT;
            }
            case 39: {
                return Component.Identifier.Key.RIGHT;
            }
            case 35: {
                return Component.Identifier.Key.END;
            }
            case 40: {
                return Component.Identifier.Key.DOWN;
            }
            case 34: {
                return Component.Identifier.Key.PAGEDOWN;
            }
            case 45: {
                return Component.Identifier.Key.INSERT;
            }
            case 46: {
                return Component.Identifier.Key.DELETE;
            }
            case 91: {
                return Component.Identifier.Key.LWIN;
            }
            case 92: {
                return Component.Identifier.Key.RWIN;
            }
            case 93: {
                return Component.Identifier.Key.APPS;
            }
            case 95: {
                return Component.Identifier.Key.SLEEP;
            }
        }
        return Component.Identifier.Key.UNKNOWN;
    }
}

/*
 * Decompiled with CFR 0.152.
 */
package net.java.games.input;

import java.io.File;
import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.List;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;
import net.java.games.input.RawDevice;
import net.java.games.input.RawDeviceInfo;
import net.java.games.input.RawInputEventQueue;
import net.java.games.input.SetupAPIDevice;
import net.java.games.util.plugins.Plugin;

public final class RawInputEnvironmentPlugin
extends ControllerEnvironment
implements Plugin {
    private static boolean supported = false;
    private final Controller[] controllers;

    static void loadLibrary(final String lib_name) {
        AccessController.doPrivileged(new PrivilegedAction(){

            public final Object run() {
                try {
                    String lib_path = System.getProperty("net.java.games.input.librarypath");
                    if (lib_path != null) {
                        System.load(lib_path + File.separator + System.mapLibraryName(lib_name));
                    } else {
                        System.loadLibrary(lib_name);
                    }
                }
                catch (UnsatisfiedLinkError e) {
                    e.printStackTrace();
                    supported = false;
                }
                return null;
            }
        });
    }

    static String getPrivilegedProperty(final String property) {
        return (String)AccessController.doPrivileged(new PrivilegedAction(){

            public Object run() {
                return System.getProperty(property);
            }
        });
    }

    static String getPrivilegedProperty(final String property, final String default_value) {
        return (String)AccessController.doPrivileged(new PrivilegedAction(){

            public Object run() {
                return System.getProperty(property, default_value);
            }
        });
    }

    public RawInputEnvironmentPlugin() {
        Controller[] controllers = new Controller[]{};
        if (this.isSupported()) {
            try {
                RawInputEventQueue queue = new RawInputEventQueue();
                controllers = this.enumControllers(queue);
            }
            catch (IOException e) {
                RawInputEnvironmentPlugin.logln("Failed to enumerate devices: " + e.getMessage());
            }
        }
        this.controllers = controllers;
    }

    public final Controller[] getControllers() {
        return this.controllers;
    }

    private static final SetupAPIDevice lookupSetupAPIDevice(String device_name, List setupapi_devices) {
        device_name = device_name.replaceAll("#", "\\\\").toUpperCase();
        for (int i = 0; i < setupapi_devices.size(); ++i) {
            SetupAPIDevice device = (SetupAPIDevice)setupapi_devices.get(i);
            if (device_name.indexOf(device.getInstanceId().toUpperCase()) == -1) continue;
            return device;
        }
        return null;
    }

    private static final void createControllersFromDevices(RawInputEventQueue queue, List controllers, List devices, List setupapi_devices) throws IOException {
        ArrayList<RawDevice> active_devices = new ArrayList<RawDevice>();
        for (int i = 0; i < devices.size(); ++i) {
            RawDeviceInfo info;
            Controller controller;
            RawDevice device = (RawDevice)devices.get(i);
            SetupAPIDevice setupapi_device = RawInputEnvironmentPlugin.lookupSetupAPIDevice(device.getName(), setupapi_devices);
            if (setupapi_device == null || (controller = (info = device.getInfo()).createControllerFromDevice(device, setupapi_device)) == null) continue;
            controllers.add(controller);
            active_devices.add(device);
        }
        queue.start(active_devices);
    }

    private static final native void enumerateDevices(RawInputEventQueue var0, List var1) throws IOException;

    private final Controller[] enumControllers(RawInputEventQueue queue) throws IOException {
        ArrayList controllers = new ArrayList();
        ArrayList devices = new ArrayList();
        RawInputEnvironmentPlugin.enumerateDevices(queue, devices);
        List setupapi_devices = RawInputEnvironmentPlugin.enumSetupAPIDevices();
        RawInputEnvironmentPlugin.createControllersFromDevices(queue, controllers, devices, setupapi_devices);
        Controller[] controllers_array = new Controller[controllers.size()];
        controllers.toArray(controllers_array);
        return controllers_array;
    }

    public boolean isSupported() {
        return supported;
    }

    private static final List enumSetupAPIDevices() throws IOException {
        ArrayList devices = new ArrayList();
        RawInputEnvironmentPlugin.nEnumSetupAPIDevices(RawInputEnvironmentPlugin.getKeyboardClassGUID(), devices);
        RawInputEnvironmentPlugin.nEnumSetupAPIDevices(RawInputEnvironmentPlugin.getMouseClassGUID(), devices);
        return devices;
    }

    private static final native void nEnumSetupAPIDevices(byte[] var0, List var1) throws IOException;

    private static final native byte[] getKeyboardClassGUID();

    private static final native byte[] getMouseClassGUID();

    static {
        String osName = RawInputEnvironmentPlugin.getPrivilegedProperty("os.name", "").trim();
        if (osName.startsWith("Windows")) {
            supported = true;
            if ("x86".equals(RawInputEnvironmentPlugin.getPrivilegedProperty("os.arch"))) {
                RawInputEnvironmentPlugin.loadLibrary("jinput-raw");
            } else {
                RawInputEnvironmentPlugin.loadLibrary("jinput-raw_64");
            }
        }
    }
}

/*
 * Decompiled with CFR 0.152.
 */
package net.java.games.input;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import net.java.games.input.DummyWindow;
import net.java.games.input.RawDevice;
import net.java.games.input.RawDeviceInfo;

final class RawInputEventQueue {
    private final Object monitor = new Object();
    private List devices;

    RawInputEventQueue() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void start(List devices) throws IOException {
        this.devices = devices;
        QueueThread queue = new QueueThread();
        Object object = this.monitor;
        synchronized (object) {
            queue.start();
            while (!queue.isInitialized()) {
                try {
                    this.monitor.wait();
                }
                catch (InterruptedException e) {}
            }
        }
        if (queue.getException() != null) {
            throw queue.getException();
        }
    }

    private final RawDevice lookupDevice(long handle) {
        for (int i = 0; i < this.devices.size(); ++i) {
            RawDevice device = (RawDevice)this.devices.get(i);
            if (device.getHandle() != handle) continue;
            return device;
        }
        return null;
    }

    private final void addMouseEvent(long handle, long millis, int flags, int button_flags, int button_data, long raw_buttons, long last_x, long last_y, long extra_information) {
        RawDevice device = this.lookupDevice(handle);
        if (device == null) {
            return;
        }
        device.addMouseEvent(millis, flags, button_flags, button_data, raw_buttons, last_x, last_y, extra_information);
    }

    private final void addKeyboardEvent(long handle, long millis, int make_code, int flags, int vkey, int message, long extra_information) {
        RawDevice device = this.lookupDevice(handle);
        if (device == null) {
            return;
        }
        device.addKeyboardEvent(millis, make_code, flags, vkey, message, extra_information);
    }

    private final void poll(DummyWindow window) throws IOException {
        this.nPoll(window.getHwnd());
    }

    private final native void nPoll(long var1) throws IOException;

    private static final void registerDevices(DummyWindow window, RawDeviceInfo[] devices) throws IOException {
        RawInputEventQueue.nRegisterDevices(0, window.getHwnd(), devices);
    }

    private static final native void nRegisterDevices(int var0, long var1, RawDeviceInfo[] var3) throws IOException;

    private final class QueueThread
    extends Thread {
        private boolean initialized;
        private DummyWindow window;
        private IOException exception;

        public QueueThread() {
            this.setDaemon(true);
        }

        public final boolean isInitialized() {
            return this.initialized;
        }

        public final IOException getException() {
            return this.exception;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public final void run() {
            try {
                this.window = new DummyWindow();
            }
            catch (IOException e) {
                this.exception = e;
            }
            this.initialized = true;
            Object e = RawInputEventQueue.this.monitor;
            synchronized (e) {
                RawInputEventQueue.this.monitor.notify();
            }
            if (this.exception != null) {
                return;
            }
            HashSet<RawDeviceInfo> active_infos = new HashSet<RawDeviceInfo>();
            try {
                for (int i = 0; i < RawInputEventQueue.this.devices.size(); ++i) {
                    RawDevice device = (RawDevice)RawInputEventQueue.this.devices.get(i);
                    active_infos.add(device.getInfo());
                }
                RawDeviceInfo[] active_infos_array = new RawDeviceInfo[active_infos.size()];
                active_infos.toArray(active_infos_array);
                try {
                    RawInputEventQueue.registerDevices(this.window, active_infos_array);
                    while (!this.isInterrupted()) {
                        RawInputEventQueue.this.poll(this.window);
                    }
                }
                finally {
                    this.window.destroy();
                }
            }
            catch (IOException e2) {
                this.exception = e2;
            }
        }
    }
}

/*
 * Decompiled with CFR 0.152.
 */
package net.java.games.input;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import net.java.games.input.AbstractComponent;
import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.Event;
import net.java.games.input.Keyboard;
import net.java.games.input.RawDevice;
import net.java.games.input.RawIdentifierMap;
import net.java.games.input.RawKeyboardEvent;
import net.java.games.input.Rumbler;

final class RawKeyboard
extends Keyboard {
    private final RawKeyboardEvent raw_event = new RawKeyboardEvent();
    private final RawDevice device;

    protected RawKeyboard(String name, RawDevice device, Controller[] children, Rumbler[] rumblers) throws IOException {
        super(name, RawKeyboard.createKeyboardComponents(device), children, rumblers);
        this.device = device;
    }

    private static final Component[] createKeyboardComponents(RawDevice device) {
        ArrayList<Key> components = new ArrayList<Key>();
        Field[] vkey_fields = RawIdentifierMap.class.getFields();
        for (int i = 0; i < vkey_fields.length; ++i) {
            Field vkey_field = vkey_fields[i];
            try {
                int vkey_code;
                Component.Identifier.Key key_id;
                if (!Modifier.isStatic(vkey_field.getModifiers()) || vkey_field.getType() != Integer.TYPE || (key_id = RawIdentifierMap.mapVKey(vkey_code = vkey_field.getInt(null))) == Component.Identifier.Key.UNKNOWN) continue;
                components.add(new Key(device, vkey_code, key_id));
                continue;
            }
            catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return components.toArray(new Component[0]);
    }

    protected final synchronized boolean getNextDeviceEvent(Event event) throws IOException {
        Component key;
        while (true) {
            if (!this.device.getNextKeyboardEvent(this.raw_event)) {
                return false;
            }
            int vkey = this.raw_event.getVKey();
            Component.Identifier.Key key_id = RawIdentifierMap.mapVKey(vkey);
            key = this.getComponent(key_id);
            if (key == null) continue;
            int message = this.raw_event.getMessage();
            if (message == 256 || message == 260) {
                event.set(key, 1.0f, this.raw_event.getNanos());
                return true;
            }
            if (message == 257 || message == 261) break;
        }
        event.set(key, 0.0f, this.raw_event.getNanos());
        return true;
    }

    public final void pollDevice() throws IOException {
        this.device.pollKeyboard();
    }

    protected final void setDeviceEventQueueSize(int size) throws IOException {
        this.device.setBufferSize(size);
    }

    static final class Key
    extends AbstractComponent {
        private final RawDevice device;
        private final int vkey_code;

        public Key(RawDevice device, int vkey_code, Component.Identifier.Key key_id) {
            super(key_id.getName(), key_id);
            this.device = device;
            this.vkey_code = vkey_code;
        }

        protected final float poll() throws IOException {
            return this.device.isKeyDown(this.vkey_code) ? 1.0f : 0.0f;
        }

        public final boolean isAnalog() {
            return false;
        }

        public final boolean isRelative() {
            return false;
        }
    }
}

/*
 * Decompiled with CFR 0.152.
 */
package net.java.games.input;

final class RawKeyboardEvent {
    private long millis;
    private int make_code;
    private int flags;
    private int vkey;
    private int message;
    private long extra_information;

    RawKeyboardEvent() {
    }

    public final void set(long millis, int make_code, int flags, int vkey, int message, long extra_information) {
        this.millis = millis;
        this.make_code = make_code;
        this.flags = flags;
        this.vkey = vkey;
        this.message = message;
        this.extra_information = extra_information;
    }

    public final void set(RawKeyboardEvent event) {
        this.set(event.millis, event.make_code, event.flags, event.vkey, event.message, event.extra_information);
    }

    public final int getVKey() {
        return this.vkey;
    }

    public final int getMessage() {
        return this.message;
    }

    public final long getNanos() {
        return this.millis * 1000000L;
    }
}

/*
 * Decompiled with CFR 0.152.
 */
package net.java.games.input;

import java.io.IOException;
import net.java.games.input.Controller;
import net.java.games.input.RawDevice;
import net.java.games.input.RawDeviceInfo;
import net.java.games.input.RawKeyboard;
import net.java.games.input.Rumbler;
import net.java.games.input.SetupAPIDevice;

class RawKeyboardInfo
extends RawDeviceInfo {
    private final RawDevice device;
    private final int type;
    private final int sub_type;
    private final int keyboard_mode;
    private final int num_function_keys;
    private final int num_indicators;
    private final int num_keys_total;

    public RawKeyboardInfo(RawDevice device, int type, int sub_type, int keyboard_mode, int num_function_keys, int num_indicators, int num_keys_total) {
        this.device = device;
        this.type = type;
        this.sub_type = sub_type;
        this.keyboard_mode = keyboard_mode;
        this.num_function_keys = num_function_keys;
        this.num_indicators = num_indicators;
        this.num_keys_total = num_keys_total;
    }

    public final int getUsage() {
        return 6;
    }

    public final int getUsagePage() {
        return 1;
    }

    public final long getHandle() {
        return this.device.getHandle();
    }

    public final Controller createControllerFromDevice(RawDevice device, SetupAPIDevice setupapi_device) throws IOException {
        return new RawKeyboard(setupapi_device.getName(), device, new Controller[0], new Rumbler[0]);
    }
}

/*
 * Decompiled with CFR 0.152.
 */
package net.java.games.input;

import java.io.IOException;
import net.java.games.input.AbstractComponent;
import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.Event;
import net.java.games.input.Mouse;
import net.java.games.input.RawDevice;
import net.java.games.input.RawMouseEvent;
import net.java.games.input.Rumbler;

final class RawMouse
extends Mouse {
    private static final int EVENT_DONE = 1;
    private static final int EVENT_X = 2;
    private static final int EVENT_Y = 3;
    private static final int EVENT_Z = 4;
    private static final int EVENT_BUTTON_0 = 5;
    private static final int EVENT_BUTTON_1 = 6;
    private static final int EVENT_BUTTON_2 = 7;
    private static final int EVENT_BUTTON_3 = 8;
    private static final int EVENT_BUTTON_4 = 9;
    private final RawDevice device;
    private final RawMouseEvent current_event = new RawMouseEvent();
    private int event_state = 1;

    protected RawMouse(String name, RawDevice device, Component[] components, Controller[] children, Rumbler[] rumblers) throws IOException {
        super(name, components, children, rumblers);
        this.device = device;
    }

    public final void pollDevice() throws IOException {
        this.device.pollMouse();
    }

    private static final boolean makeButtonEvent(RawMouseEvent mouse_event, Event event, Component button_component, int down_flag, int up_flag) {
        if ((mouse_event.getButtonFlags() & down_flag) != 0) {
            event.set(button_component, 1.0f, mouse_event.getNanos());
            return true;
        }
        if ((mouse_event.getButtonFlags() & up_flag) != 0) {
            event.set(button_component, 0.0f, mouse_event.getNanos());
            return true;
        }
        return false;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected final synchronized boolean getNextDeviceEvent(Event event) throws IOException {
        block11: while (true) {
            switch (this.event_state) {
                case 1: {
                    if (!this.device.getNextMouseEvent(this.current_event)) {
                        return false;
                    }
                    this.event_state = 2;
                    continue block11;
                }
                case 2: {
                    int rel_x = this.device.getEventRelativeX();
                    this.event_state = 3;
                    if (rel_x == 0) continue block11;
                    event.set(this.getX(), rel_x, this.current_event.getNanos());
                    return true;
                }
                case 3: {
                    int rel_y = this.device.getEventRelativeY();
                    this.event_state = 4;
                    if (rel_y == 0) continue block11;
                    event.set(this.getY(), rel_y, this.current_event.getNanos());
                    return true;
                }
                case 4: {
                    int wheel = this.current_event.getWheelDelta();
                    this.event_state = 5;
                    if (wheel == 0) continue block11;
                    event.set(this.getWheel(), wheel, this.current_event.getNanos());
                    return true;
                }
                case 5: {
                    this.event_state = 6;
                    if (!RawMouse.makeButtonEvent(this.current_event, event, this.getPrimaryButton(), 1, 2)) continue block11;
                    return true;
                }
                case 6: {
                    this.event_state = 7;
                    if (!RawMouse.makeButtonEvent(this.current_event, event, this.getSecondaryButton(), 4, 8)) continue block11;
                    return true;
                }
                case 7: {
                    this.event_state = 8;
                    if (!RawMouse.makeButtonEvent(this.current_event, event, this.getTertiaryButton(), 16, 32)) continue block11;
                    return true;
                }
                case 8: {
                    this.event_state = 9;
                    if (!RawMouse.makeButtonEvent(this.current_event, event, this.getButton3(), 64, 128)) continue block11;
                    return true;
                }
                case 9: {
                    this.event_state = 1;
                    if (RawMouse.makeButtonEvent(this.current_event, event, this.getButton4(), 256, 512)) return true;
                    continue block11;
                }
            }
            break;
        }
        throw new RuntimeException("Unknown event state: " + this.event_state);
    }

    protected final void setDeviceEventQueueSize(int size) throws IOException {
        this.device.setBufferSize(size);
    }

    static final class Button
    extends AbstractComponent {
        private final RawDevice device;
        private final int button_id;

        public Button(RawDevice device, Component.Identifier.Button id, int button_id) {
            super(id.getName(), id);
            this.device = device;
            this.button_id = button_id;
        }

        protected final float poll() throws IOException {
            return this.device.getButtonState(this.button_id) ? 1.0f : 0.0f;
        }

        public final boolean isAnalog() {
            return false;
        }

        public final boolean isRelative() {
            return false;
        }
    }

    static final class Axis
    extends AbstractComponent {
        private final RawDevice device;

        public Axis(RawDevice device, Component.Identifier.Axis axis) {
            super(axis.getName(), axis);
            this.device = device;
        }

        public final boolean isRelative() {
            return true;
        }

        public final boolean isAnalog() {
            return true;
        }

        protected final float poll() throws IOException {
            if (this.getIdentifier() == Component.Identifier.Axis.X) {
                return this.device.getRelativeX();
            }
            if (this.getIdentifier() == Component.Identifier.Axis.Y) {
                return this.device.getRelativeY();
            }
            if (this.getIdentifier() == Component.Identifier.Axis.Z) {
                return this.device.getWheel();
            }
            throw new RuntimeException("Unknown raw axis: " + this.getIdentifier());
        }
    }
}

/*
 * Decompiled with CFR 0.152.
 */
package net.java.games.input;

final class RawMouseEvent {
    private static final int WHEEL_SCALE = 120;
    private long millis;
    private int flags;
    private int button_flags;
    private int button_data;
    private long raw_buttons;
    private long last_x;
    private long last_y;
    private long extra_information;

    RawMouseEvent() {
    }

    public final void set(long millis, int flags, int button_flags, int button_data, long raw_buttons, long last_x, long last_y, long extra_information) {
        this.millis = millis;
        this.flags = flags;
        this.button_flags = button_flags;
        this.button_data = button_data;
        this.raw_buttons = raw_buttons;
        this.last_x = last_x;
        this.last_y = last_y;
        this.extra_information = extra_information;
    }

    public final void set(RawMouseEvent event) {
        this.set(event.millis, event.flags, event.button_flags, event.button_data, event.raw_buttons, event.last_x, event.last_y, event.extra_information);
    }

    public final int getWheelDelta() {
        return this.button_data / 120;
    }

    private final int getButtonData() {
        return this.button_data;
    }

    public final int getFlags() {
        return this.flags;
    }

    public final int getButtonFlags() {
        return this.button_flags;
    }

    public final int getLastX() {
        return (int)this.last_x;
    }

    public final int getLastY() {
        return (int)this.last_y;
    }

    public final long getRawButtons() {
        return this.raw_buttons;
    }

    public final long getNanos() {
        return this.millis * 1000000L;
    }
}

/*
 * Decompiled with CFR 0.152.
 */
package net.java.games.input;

import java.io.IOException;
import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.DIIdentifierMap;
import net.java.games.input.RawDevice;
import net.java.games.input.RawDeviceInfo;
import net.java.games.input.RawMouse;
import net.java.games.input.Rumbler;
import net.java.games.input.SetupAPIDevice;

class RawMouseInfo
extends RawDeviceInfo {
    private final RawDevice device;
    private final int id;
    private final int num_buttons;
    private final int sample_rate;

    public RawMouseInfo(RawDevice device, int id, int num_buttons, int sample_rate) {
        this.device = device;
        this.id = id;
        this.num_buttons = num_buttons;
        this.sample_rate = sample_rate;
    }

    public final int getUsage() {
        return 2;
    }

    public final int getUsagePage() {
        return 1;
    }

    public final long getHandle() {
        return this.device.getHandle();
    }

    public final Controller createControllerFromDevice(RawDevice device, SetupAPIDevice setupapi_device) throws IOException {
        if (this.num_buttons == 0) {
            return null;
        }
        Component[] components = new Component[3 + this.num_buttons];
        int index = 0;
        components[index++] = new RawMouse.Axis(device, Component.Identifier.Axis.X);
        components[index++] = new RawMouse.Axis(device, Component.Identifier.Axis.Y);
        components[index++] = new RawMouse.Axis(device, Component.Identifier.Axis.Z);
        for (int i = 0; i < this.num_buttons; ++i) {
            Component.Identifier.Button id = DIIdentifierMap.mapMouseButtonIdentifier(DIIdentifierMap.getButtonIdentifier(i));
            components[index++] = new RawMouse.Button(device, id, i);
        }
        RawMouse mouse = new RawMouse(setupapi_device.getName(), device, components, new Controller[0], new Rumbler[0]);
        return mouse;
    }
}

/*
 * Decompiled with CFR 0.152.
 */
package net.java.games.input;

import net.java.games.input.Component;

public interface Rumbler {
    public void rumble(float var1);

    public String getAxisName();

    public Component.Identifier getAxisIdentifier();
}

/*
 * Decompiled with CFR 0.152.
 */
package net.java.games.input;

final class SetupAPIDevice {
    private final String device_instance_id;
    private final String device_name;

    public SetupAPIDevice(String device_instance_id, String device_name) {
        this.device_instance_id = device_instance_id;
        this.device_name = device_name;
    }

    public final String getName() {
        return this.device_name;
    }

    public final String getInstanceId() {
        return this.device_instance_id;
    }
}

/*
 * Decompiled with CFR 0.152.
 */
package net.java.games.input;

public interface Usage {
}

/*
 * Decompiled with CFR 0.152.
 */
package net.java.games.input;

import java.lang.reflect.Method;
import net.java.games.input.ButtonUsage;
import net.java.games.input.GenericDesktopUsage;
import net.java.games.input.KeyboardUsage;
import net.java.games.input.Usage;

final class UsagePage {
    private static final UsagePage[] map = new UsagePage[255];
    public static final UsagePage UNDEFINED = new UsagePage(0);
    public static final UsagePage GENERIC_DESKTOP = new UsagePage(1, GenericDesktopUsage.class);
    public static final UsagePage SIMULATION = new UsagePage(2);
    public static final UsagePage VR = new UsagePage(3);
    public static final UsagePage SPORT = new UsagePage(4);
    public static final UsagePage GAME = new UsagePage(5);
    public static final UsagePage KEYBOARD_OR_KEYPAD = new UsagePage(7, KeyboardUsage.class);
    public static final UsagePage LEDS = new UsagePage(8);
    public static final UsagePage BUTTON = new UsagePage(9, ButtonUsage.class);
    public static final UsagePage ORDINAL = new UsagePage(10);
    public static final UsagePage TELEPHONY = new UsagePage(11);
    public static final UsagePage CONSUMER = new UsagePage(12);
    public static final UsagePage DIGITIZER = new UsagePage(13);
    public static final UsagePage PID = new UsagePage(15);
    public static final UsagePage UNICODE = new UsagePage(16);
    public static final UsagePage ALPHANUMERIC_DISPLAY = new UsagePage(20);
    public static final UsagePage POWER_DEVICE = new UsagePage(132);
    public static final UsagePage BATTERY_SYSTEM = new UsagePage(133);
    public static final UsagePage BAR_CODE_SCANNER = new UsagePage(140);
    public static final UsagePage SCALE = new UsagePage(141);
    public static final UsagePage CAMERACONTROL = new UsagePage(144);
    public static final UsagePage ARCADE = new UsagePage(145);
    private final Class usage_class;
    private final int usage_page_id;

    public static final UsagePage map(int page_id) {
        if (page_id < 0 || page_id >= map.length) {
            return null;
        }
        return map[page_id];
    }

    private UsagePage(int page_id, Class usage_class) {
        UsagePage.map[page_id] = this;
        this.usage_class = usage_class;
        this.usage_page_id = page_id;
    }

    private UsagePage(int page_id) {
        this(page_id, null);
    }

    public final String toString() {
        return "UsagePage (0x" + Integer.toHexString(this.usage_page_id) + ")";
    }

    public final Usage mapUsage(int usage_id) {
        if (this.usage_class == null) {
            return null;
        }
        try {
            Method map_method = this.usage_class.getMethod("map", Integer.TYPE);
            Object result = map_method.invoke(null, new Integer(usage_id));
            return (Usage)result;
        }
        catch (Exception e) {
            throw new Error(e);
        }
    }
}

/*
 * Decompiled with CFR 0.152.
 */
package net.java.games.input;

import net.java.games.input.Usage;
import net.java.games.input.UsagePage;

class UsagePair {
    private final UsagePage usage_page;
    private final Usage usage;

    public UsagePair(UsagePage usage_page, Usage usage) {
        this.usage_page = usage_page;
        this.usage = usage;
    }

    public final UsagePage getUsagePage() {
        return this.usage_page;
    }

    public final Usage getUsage() {
        return this.usage;
    }

    public final int hashCode() {
        return this.usage.hashCode() ^ this.usage_page.hashCode();
    }

    public final boolean equals(Object other) {
        if (!(other instanceof UsagePair)) {
            return false;
        }
        UsagePair other_pair = (UsagePair)other;
        return other_pair.usage.equals(this.usage) && other_pair.usage_page.equals(this.usage_page);
    }

    public final String toString() {
        return "UsagePair: (page = " + this.usage_page + ", usage = " + this.usage + ")";
    }
}

/*
 * Decompiled with CFR 0.152.
 */
package net.java.games.input;

public final class Version {
    private static final String apiVersion = "2.0.5";
    private static final String buildNumber = "1088";
    private static final String antBuildNumberToken = "@BUILD_NUMBER@";
    private static final String antAPIVersionToken = "@API_VERSION@";

    private Version() {
    }

    public static String getVersion() {
        String version = "Unversioned";
        if (!antAPIVersionToken.equals(apiVersion)) {
            version = apiVersion;
        }
        if (!antBuildNumberToken.equals(buildNumber)) {
            version = version + "-b1088";
        }
        return version;
    }
}

/*
 * Decompiled with CFR 0.152.
 */
package net.java.games.input;

import net.java.games.input.Component;
import net.java.games.input.Event;
import net.java.games.input.WinTabComponent;
import net.java.games.input.WinTabContext;
import net.java.games.input.WinTabPacket;

public class WinTabButtonComponent
extends WinTabComponent {
    private int index;

    protected WinTabButtonComponent(WinTabContext context, int parentDevice, String name, Component.Identifier id, int index) {
        super(context, parentDevice, name, id);
        this.index = index;
    }

    public Event processPacket(WinTabPacket packet) {
        float newValue;
        Event newEvent = null;
        float f = newValue = (packet.PK_BUTTONS & (int)Math.pow(2.0, this.index)) > 0 ? 1.0f : 0.0f;
        if (newValue != this.getPollData()) {
            this.lastKnownValue = newValue;
            newEvent = new Event();
            newEvent.set(this, newValue, packet.PK_TIME * 1000L);
            return newEvent;
        }
        return newEvent;
    }
}

/*
 * Decompiled with CFR 0.152.
 */
package net.java.games.input;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.java.games.input.AbstractComponent;
import net.java.games.input.Component;
import net.java.games.input.Event;
import net.java.games.input.WinTabButtonComponent;
import net.java.games.input.WinTabContext;
import net.java.games.input.WinTabCursorComponent;
import net.java.games.input.WinTabPacket;

public class WinTabComponent
extends AbstractComponent {
    public static final int XAxis = 1;
    public static final int YAxis = 2;
    public static final int ZAxis = 3;
    public static final int NPressureAxis = 4;
    public static final int TPressureAxis = 5;
    public static final int OrientationAxis = 6;
    public static final int RotationAxis = 7;
    private int min;
    private int max;
    protected float lastKnownValue;
    private boolean analog;
    static /* synthetic */ Class class$net$java$games$input$Component$Identifier$Button;

    protected WinTabComponent(WinTabContext context, int parentDevice, String name, Component.Identifier id, int min, int max) {
        super(name, id);
        this.min = min;
        this.max = max;
        this.analog = true;
    }

    protected WinTabComponent(WinTabContext context, int parentDevice, String name, Component.Identifier id) {
        super(name, id);
        this.min = 0;
        this.max = 1;
        this.analog = false;
    }

    protected float poll() throws IOException {
        return this.lastKnownValue;
    }

    public boolean isAnalog() {
        return this.analog;
    }

    public boolean isRelative() {
        return false;
    }

    public static List createComponents(WinTabContext context, int parentDevice, int axisId, int[] axisRanges) {
        ArrayList<WinTabComponent> components = new ArrayList<WinTabComponent>();
        switch (axisId) {
            case 1: {
                Component.Identifier.Axis id = Component.Identifier.Axis.X;
                components.add(new WinTabComponent(context, parentDevice, id.getName(), id, axisRanges[0], axisRanges[1]));
                break;
            }
            case 2: {
                Component.Identifier.Axis id = Component.Identifier.Axis.Y;
                components.add(new WinTabComponent(context, parentDevice, id.getName(), id, axisRanges[0], axisRanges[1]));
                break;
            }
            case 3: {
                Component.Identifier.Axis id = Component.Identifier.Axis.Z;
                components.add(new WinTabComponent(context, parentDevice, id.getName(), id, axisRanges[0], axisRanges[1]));
                break;
            }
            case 4: {
                Component.Identifier.Axis id = Component.Identifier.Axis.X_FORCE;
                components.add(new WinTabComponent(context, parentDevice, id.getName(), id, axisRanges[0], axisRanges[1]));
                break;
            }
            case 5: {
                Component.Identifier.Axis id = Component.Identifier.Axis.Y_FORCE;
                components.add(new WinTabComponent(context, parentDevice, id.getName(), id, axisRanges[0], axisRanges[1]));
                break;
            }
            case 6: {
                Component.Identifier.Axis id = Component.Identifier.Axis.RX;
                components.add(new WinTabComponent(context, parentDevice, id.getName(), id, axisRanges[0], axisRanges[1]));
                id = Component.Identifier.Axis.RY;
                components.add(new WinTabComponent(context, parentDevice, id.getName(), id, axisRanges[2], axisRanges[3]));
                id = Component.Identifier.Axis.RZ;
                components.add(new WinTabComponent(context, parentDevice, id.getName(), id, axisRanges[4], axisRanges[5]));
                break;
            }
            case 7: {
                Component.Identifier.Axis id = Component.Identifier.Axis.RX;
                components.add(new WinTabComponent(context, parentDevice, id.getName(), id, axisRanges[0], axisRanges[1]));
                id = Component.Identifier.Axis.RY;
                components.add(new WinTabComponent(context, parentDevice, id.getName(), id, axisRanges[2], axisRanges[3]));
                id = Component.Identifier.Axis.RZ;
                components.add(new WinTabComponent(context, parentDevice, id.getName(), id, axisRanges[4], axisRanges[5]));
            }
        }
        return components;
    }

    public static Collection createButtons(WinTabContext context, int deviceIndex, int numberOfButtons) {
        ArrayList<WinTabButtonComponent> buttons = new ArrayList<WinTabButtonComponent>();
        for (int i = 0; i < numberOfButtons; ++i) {
            try {
                Class buttonIdClass = class$net$java$games$input$Component$Identifier$Button == null ? WinTabComponent.class$("net.java.games.input.Component$Identifier$Button") : class$net$java$games$input$Component$Identifier$Button;
                Field idField = buttonIdClass.getField("_" + i);
                Component.Identifier id = (Component.Identifier)idField.get(null);
                buttons.add(new WinTabButtonComponent(context, deviceIndex, id.getName(), id, i));
                continue;
            }
            catch (SecurityException e) {
                e.printStackTrace();
                continue;
            }
            catch (NoSuchFieldException e) {
                e.printStackTrace();
                continue;
            }
            catch (IllegalArgumentException e) {
                e.printStackTrace();
                continue;
            }
            catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return buttons;
    }

    public Event processPacket(WinTabPacket packet) {
        float newValue = this.lastKnownValue;
        if (this.getIdentifier() == Component.Identifier.Axis.X) {
            newValue = this.normalise(packet.PK_X);
        }
        if (this.getIdentifier() == Component.Identifier.Axis.Y) {
            newValue = this.normalise(packet.PK_Y);
        }
        if (this.getIdentifier() == Component.Identifier.Axis.Z) {
            newValue = this.normalise(packet.PK_Z);
        }
        if (this.getIdentifier() == Component.Identifier.Axis.X_FORCE) {
            newValue = this.normalise(packet.PK_NORMAL_PRESSURE);
        }
        if (this.getIdentifier() == Component.Identifier.Axis.Y_FORCE) {
            newValue = this.normalise(packet.PK_TANGENT_PRESSURE);
        }
        if (this.getIdentifier() == Component.Identifier.Axis.RX) {
            newValue = this.normalise(packet.PK_ORIENTATION_ALT);
        }
        if (this.getIdentifier() == Component.Identifier.Axis.RY) {
            newValue = this.normalise(packet.PK_ORIENTATION_AZ);
        }
        if (this.getIdentifier() == Component.Identifier.Axis.RZ) {
            newValue = this.normalise(packet.PK_ORIENTATION_TWIST);
        }
        if (newValue != this.getPollData()) {
            this.lastKnownValue = newValue;
            Event newEvent = new Event();
            newEvent.set(this, newValue, packet.PK_TIME * 1000L);
            return newEvent;
        }
        return null;
    }

    private float normalise(float value) {
        if (this.max == this.min) {
            return value;
        }
        float bottom = this.max - this.min;
        return (value - (float)this.min) / bottom;
    }

    public static Collection createCursors(WinTabContext context, int deviceIndex, String[] cursorNames) {
        ArrayList<WinTabCursorComponent> cursors = new ArrayList<WinTabCursorComponent>();
        for (int i = 0; i < cursorNames.length; ++i) {
            Component.Identifier.Button id = cursorNames[i].matches("Puck") ? Component.Identifier.Button.TOOL_FINGER : (cursorNames[i].matches("Eraser.*") ? Component.Identifier.Button.TOOL_RUBBER : Component.Identifier.Button.TOOL_PEN);
            cursors.add(new WinTabCursorComponent(context, deviceIndex, id.getName(), id, i));
        }
        return cursors;
    }
}

/*
 * Decompiled with CFR 0.152.
 */
package net.java.games.input;

import java.util.ArrayList;
import net.java.games.input.Controller;
import net.java.games.input.DummyWindow;
import net.java.games.input.WinTabDevice;
import net.java.games.input.WinTabPacket;

public class WinTabContext {
    private DummyWindow window;
    private long hCTX;
    private Controller[] controllers;

    public WinTabContext(DummyWindow window) {
        this.window = window;
    }

    public Controller[] getControllers() {
        if (this.hCTX == 0L) {
            throw new IllegalStateException("Context must be open before getting the controllers");
        }
        return this.controllers;
    }

    public synchronized void open() {
        this.hCTX = WinTabContext.nOpen(this.window.getHwnd());
        ArrayList<WinTabDevice> devices = new ArrayList<WinTabDevice>();
        int numSupportedDevices = WinTabContext.nGetNumberOfSupportedDevices();
        for (int i = 0; i < numSupportedDevices; ++i) {
            WinTabDevice newDevice = WinTabDevice.createDevice(this, i);
            if (newDevice == null) continue;
            devices.add(newDevice);
        }
        this.controllers = devices.toArray(new Controller[0]);
    }

    public synchronized void close() {
        WinTabContext.nClose(this.hCTX);
    }

    public synchronized void processEvents() {
        WinTabPacket[] packets = WinTabContext.nGetPackets(this.hCTX);
        for (int i = 0; i < packets.length; ++i) {
            ((WinTabDevice)this.getControllers()[0]).processPacket(packets[i]);
        }
    }

    private static final native int nGetNumberOfSupportedDevices();

    private static final native long nOpen(long var0);

    private static final native void nClose(long var0);

    private static final native WinTabPacket[] nGetPackets(long var0);
}

/*
 * Decompiled with CFR 0.152.
 */
package net.java.games.input;

import net.java.games.input.Component;
import net.java.games.input.Event;
import net.java.games.input.WinTabComponent;
import net.java.games.input.WinTabContext;
import net.java.games.input.WinTabPacket;

public class WinTabCursorComponent
extends WinTabComponent {
    private int index;

    protected WinTabCursorComponent(WinTabContext context, int parentDevice, String name, Component.Identifier id, int index) {
        super(context, parentDevice, name, id);
        this.index = index;
    }

    public Event processPacket(WinTabPacket packet) {
        Event newEvent = null;
        if (packet.PK_CURSOR == this.index && this.lastKnownValue == 0.0f) {
            this.lastKnownValue = 1.0f;
            newEvent = new Event();
            newEvent.set(this, this.lastKnownValue, packet.PK_TIME * 1000L);
        } else if (packet.PK_CURSOR != this.index && this.lastKnownValue == 1.0f) {
            this.lastKnownValue = 0.0f;
            newEvent = new Event();
            newEvent.set(this, this.lastKnownValue, packet.PK_TIME * 1000L);
        }
        return newEvent;
    }
}

/*
 * Decompiled with CFR 0.152.
 */
package net.java.games.input;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.java.games.input.AbstractController;
import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.Event;
import net.java.games.input.Rumbler;
import net.java.games.input.WinTabComponent;
import net.java.games.input.WinTabContext;
import net.java.games.input.WinTabEnvironmentPlugin;
import net.java.games.input.WinTabPacket;

public class WinTabDevice
extends AbstractController {
    private WinTabContext context;
    private List eventList = new ArrayList();

    private WinTabDevice(WinTabContext context, int index, String name, Component[] components) {
        super(name, components, new Controller[0], new Rumbler[0]);
        this.context = context;
    }

    protected boolean getNextDeviceEvent(Event event) throws IOException {
        if (this.eventList.size() > 0) {
            Event ourEvent = (Event)this.eventList.remove(0);
            event.set(ourEvent);
            return true;
        }
        return false;
    }

    protected void pollDevice() throws IOException {
        this.context.processEvents();
        super.pollDevice();
    }

    public Controller.Type getType() {
        return Controller.Type.TRACKPAD;
    }

    public void processPacket(WinTabPacket packet) {
        Component[] components = this.getComponents();
        for (int i = 0; i < components.length; ++i) {
            Event event = ((WinTabComponent)components[i]).processPacket(packet);
            if (event == null) continue;
            this.eventList.add(event);
        }
    }

    public static WinTabDevice createDevice(WinTabContext context, int deviceIndex) {
        String name = WinTabDevice.nGetName(deviceIndex);
        WinTabEnvironmentPlugin.logln("Device " + deviceIndex + ", name: " + name);
        ArrayList componentsList = new ArrayList();
        int[] axisDetails = WinTabDevice.nGetAxisDetails(deviceIndex, 1);
        if (axisDetails.length == 0) {
            WinTabEnvironmentPlugin.logln("ZAxis not supported");
        } else {
            WinTabEnvironmentPlugin.logln("Xmin: " + axisDetails[0] + ", Xmax: " + axisDetails[1]);
            componentsList.addAll(WinTabComponent.createComponents(context, deviceIndex, 1, axisDetails));
        }
        axisDetails = WinTabDevice.nGetAxisDetails(deviceIndex, 2);
        if (axisDetails.length == 0) {
            WinTabEnvironmentPlugin.logln("YAxis not supported");
        } else {
            WinTabEnvironmentPlugin.logln("Ymin: " + axisDetails[0] + ", Ymax: " + axisDetails[1]);
            componentsList.addAll(WinTabComponent.createComponents(context, deviceIndex, 2, axisDetails));
        }
        axisDetails = WinTabDevice.nGetAxisDetails(deviceIndex, 3);
        if (axisDetails.length == 0) {
            WinTabEnvironmentPlugin.logln("ZAxis not supported");
        } else {
            WinTabEnvironmentPlugin.logln("Zmin: " + axisDetails[0] + ", Zmax: " + axisDetails[1]);
            componentsList.addAll(WinTabComponent.createComponents(context, deviceIndex, 3, axisDetails));
        }
        axisDetails = WinTabDevice.nGetAxisDetails(deviceIndex, 4);
        if (axisDetails.length == 0) {
            WinTabEnvironmentPlugin.logln("NPressureAxis not supported");
        } else {
            WinTabEnvironmentPlugin.logln("NPressMin: " + axisDetails[0] + ", NPressMax: " + axisDetails[1]);
            componentsList.addAll(WinTabComponent.createComponents(context, deviceIndex, 4, axisDetails));
        }
        axisDetails = WinTabDevice.nGetAxisDetails(deviceIndex, 5);
        if (axisDetails.length == 0) {
            WinTabEnvironmentPlugin.logln("TPressureAxis not supported");
        } else {
            WinTabEnvironmentPlugin.logln("TPressureAxismin: " + axisDetails[0] + ", TPressureAxismax: " + axisDetails[1]);
            componentsList.addAll(WinTabComponent.createComponents(context, deviceIndex, 5, axisDetails));
        }
        axisDetails = WinTabDevice.nGetAxisDetails(deviceIndex, 6);
        if (axisDetails.length == 0) {
            WinTabEnvironmentPlugin.logln("OrientationAxis not supported");
        } else {
            WinTabEnvironmentPlugin.logln("OrientationAxis mins/maxs: " + axisDetails[0] + "," + axisDetails[1] + ", " + axisDetails[2] + "," + axisDetails[3] + ", " + axisDetails[4] + "," + axisDetails[5]);
            componentsList.addAll(WinTabComponent.createComponents(context, deviceIndex, 6, axisDetails));
        }
        axisDetails = WinTabDevice.nGetAxisDetails(deviceIndex, 7);
        if (axisDetails.length == 0) {
            WinTabEnvironmentPlugin.logln("RotationAxis not supported");
        } else {
            WinTabEnvironmentPlugin.logln("RotationAxis is supported (by the device, not by this plugin)");
            componentsList.addAll(WinTabComponent.createComponents(context, deviceIndex, 7, axisDetails));
        }
        String[] cursorNames = WinTabDevice.nGetCursorNames(deviceIndex);
        componentsList.addAll(WinTabComponent.createCursors(context, deviceIndex, cursorNames));
        for (int i = 0; i < cursorNames.length; ++i) {
            WinTabEnvironmentPlugin.logln("Cursor " + i + "'s name: " + cursorNames[i]);
        }
        int numberOfButtons = WinTabDevice.nGetMaxButtonCount(deviceIndex);
        WinTabEnvironmentPlugin.logln("Device has " + numberOfButtons + " buttons");
        componentsList.addAll(WinTabComponent.createButtons(context, deviceIndex, numberOfButtons));
        Component[] components = componentsList.toArray(new Component[0]);
        return new WinTabDevice(context, deviceIndex, name, components);
    }

    private static final native String nGetName(int var0);

    private static final native int[] nGetAxisDetails(int var0, int var1);

    private static final native String[] nGetCursorNames(int var0);

    private static final native int nGetMaxButtonCount(int var0);
}

/*
 * Decompiled with CFR 0.152.
 */
package net.java.games.input;

import java.io.File;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.List;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;
import net.java.games.input.DummyWindow;
import net.java.games.input.WinTabContext;
import net.java.games.util.plugins.Plugin;

public class WinTabEnvironmentPlugin
extends ControllerEnvironment
implements Plugin {
    private static boolean supported = false;
    private final Controller[] controllers;
    private final List active_devices = new ArrayList();
    private final WinTabContext winTabContext;

    static void loadLibrary(final String lib_name) {
        AccessController.doPrivileged(new PrivilegedAction(){

            public final Object run() {
                try {
                    String lib_path = System.getProperty("net.java.games.input.librarypath");
                    if (lib_path != null) {
                        System.load(lib_path + File.separator + System.mapLibraryName(lib_name));
                    } else {
                        System.loadLibrary(lib_name);
                    }
                }
                catch (UnsatisfiedLinkError e) {
                    e.printStackTrace();
                    supported = false;
                }
                return null;
            }
        });
    }

    static String getPrivilegedProperty(final String property) {
        return (String)AccessController.doPrivileged(new PrivilegedAction(){

            public Object run() {
                return System.getProperty(property);
            }
        });
    }

    static String getPrivilegedProperty(final String property, final String default_value) {
        return (String)AccessController.doPrivileged(new PrivilegedAction(){

            public Object run() {
                return System.getProperty(property, default_value);
            }
        });
    }

    public WinTabEnvironmentPlugin() {
        if (this.isSupported()) {
            DummyWindow window = null;
            WinTabContext winTabContext = null;
            Controller[] controllers = new Controller[]{};
            try {
                window = new DummyWindow();
                winTabContext = new WinTabContext(window);
                try {
                    winTabContext.open();
                    controllers = winTabContext.getControllers();
                }
                catch (Exception e) {
                    window.destroy();
                    throw e;
                }
            }
            catch (Exception e) {
                WinTabEnvironmentPlugin.logln("Failed to enumerate devices: " + e.getMessage());
                e.printStackTrace();
            }
            this.controllers = controllers;
            this.winTabContext = winTabContext;
            AccessController.doPrivileged(new PrivilegedAction(){

                public final Object run() {
                    Runtime.getRuntime().addShutdownHook(new ShutdownHook());
                    return null;
                }
            });
        } else {
            this.winTabContext = null;
            this.controllers = new Controller[0];
        }
    }

    public boolean isSupported() {
        return supported;
    }

    public Controller[] getControllers() {
        return this.controllers;
    }

    static {
        String osName = WinTabEnvironmentPlugin.getPrivilegedProperty("os.name", "").trim();
        if (osName.startsWith("Windows")) {
            supported = true;
            WinTabEnvironmentPlugin.loadLibrary("jinput-wintab");
        }
    }

    private final class ShutdownHook
    extends Thread {
        private ShutdownHook() {
        }

        public final void run() {
            for (int i = 0; i < WinTabEnvironmentPlugin.this.active_devices.size(); ++i) {
            }
            WinTabEnvironmentPlugin.this.winTabContext.close();
        }
    }
}

/*
 * Decompiled with CFR 0.152.
 */
package net.java.games.input;

public class WinTabPacket {
    long PK_TIME;
    int PK_X;
    int PK_Y;
    int PK_Z;
    int PK_BUTTONS;
    int PK_NORMAL_PRESSURE;
    int PK_TANGENT_PRESSURE;
    int PK_CURSOR;
    int PK_ORIENTATION_ALT;
    int PK_ORIENTATION_AZ;
    int PK_ORIENTATION_TWIST;
}

/*
 * Decompiled with CFR 0.152.
 */
package com.esotericsoftware.minlog;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;

public class Log {
    public static final int LEVEL_NONE = 6;
    public static final int LEVEL_ERROR = 5;
    public static final int LEVEL_WARN = 4;
    public static final int LEVEL_INFO = 3;
    public static final int LEVEL_DEBUG = 2;
    public static final int LEVEL_TRACE = 1;
    private static int level = 3;
    public static boolean ERROR = level <= 5;
    public static boolean WARN = level <= 4;
    public static boolean INFO = level <= 3;
    public static boolean DEBUG = level <= 2;
    public static boolean TRACE = level <= 1;
    private static Logger logger = new Logger();

    public static void set(int level) {
        Log.level = level;
        ERROR = level <= 5;
        WARN = level <= 4;
        INFO = level <= 3;
        DEBUG = level <= 2;
        TRACE = level <= 1;
    }

    public static void NONE() {
        Log.set(6);
    }

    public static void ERROR() {
        Log.set(5);
    }

    public static void WARN() {
        Log.set(4);
    }

    public static void INFO() {
        Log.set(3);
    }

    public static void DEBUG() {
        Log.set(2);
    }

    public static void TRACE() {
        Log.set(1);
    }

    public static void setLogger(Logger logger) {
        Log.logger = logger;
    }

    public static void error(String message, Throwable ex) {
        if (ERROR) {
            logger.log(5, null, message, ex);
        }
    }

    public static void error(String category, String message, Throwable ex) {
        if (ERROR) {
            logger.log(5, category, message, ex);
        }
    }

    public static void error(String message) {
        if (ERROR) {
            logger.log(5, null, message, null);
        }
    }

    public static void error(String category, String message) {
        if (ERROR) {
            logger.log(5, category, message, null);
        }
    }

    public static void warn(String message, Throwable ex) {
        if (WARN) {
            logger.log(4, null, message, ex);
        }
    }

    public static void warn(String category, String message, Throwable ex) {
        if (WARN) {
            logger.log(4, category, message, ex);
        }
    }

    public static void warn(String message) {
        if (WARN) {
            logger.log(4, null, message, null);
        }
    }

    public static void warn(String category, String message) {
        if (WARN) {
            logger.log(4, category, message, null);
        }
    }

    public static void info(String message, Throwable ex) {
        if (INFO) {
            logger.log(3, null, message, ex);
        }
    }

    public static void info(String category, String message, Throwable ex) {
        if (INFO) {
            logger.log(3, category, message, ex);
        }
    }

    public static void info(String message) {
        if (INFO) {
            logger.log(3, null, message, null);
        }
    }

    public static void info(String category, String message) {
        if (INFO) {
            logger.log(3, category, message, null);
        }
    }

    public static void debug(String message, Throwable ex) {
        if (DEBUG) {
            logger.log(2, null, message, ex);
        }
    }

    public static void debug(String category, String message, Throwable ex) {
        if (DEBUG) {
            logger.log(2, category, message, ex);
        }
    }

    public static void debug(String message) {
        if (DEBUG) {
            logger.log(2, null, message, null);
        }
    }

    public static void debug(String category, String message) {
        if (DEBUG) {
            logger.log(2, category, message, null);
        }
    }

    public static void trace(String message, Throwable ex) {
        if (TRACE) {
            logger.log(1, null, message, ex);
        }
    }

    public static void trace(String category, String message, Throwable ex) {
        if (TRACE) {
            logger.log(1, category, message, ex);
        }
    }

    public static void trace(String message) {
        if (TRACE) {
            logger.log(1, null, message, null);
        }
    }

    public static void trace(String category, String message) {
        if (TRACE) {
            logger.log(1, category, message, null);
        }
    }

    private Log() {
    }

    public static class Logger {
        private long firstLogTime = new Date().getTime();

        public void log(int level, String category, String message, Throwable ex) {
            StringBuilder builder = new StringBuilder(256);
            long time = new Date().getTime() - this.firstLogTime;
            long minutes = time / 60000L;
            long seconds = time / 1000L % 60L;
            if (minutes <= 9L) {
                builder.append('0');
            }
            builder.append(minutes);
            builder.append(':');
            if (seconds <= 9L) {
                builder.append('0');
            }
            builder.append(seconds);
            switch (level) {
                case 5: {
                    builder.append(" ERROR: ");
                    break;
                }
                case 4: {
                    builder.append("  WARN: ");
                    break;
                }
                case 3: {
                    builder.append("  INFO: ");
                    break;
                }
                case 2: {
                    builder.append(" DEBUG: ");
                    break;
                }
                case 1: {
                    builder.append(" TRACE: ");
                }
            }
            if (category != null) {
                builder.append('[');
                builder.append(category);
                builder.append("] ");
            }
            builder.append(message);
            if (ex != null) {
                StringWriter writer = new StringWriter(256);
                ex.printStackTrace(new PrintWriter(writer));
                builder.append('\n');
                builder.append(writer.toString().trim());
            }
            this.print(builder.toString());
        }

        protected void print(String message) {
            System.out.println(message);
        }
    }
}

/*
 * Decompiled with CFR 0.152.
 */
package org.objenesis;

import org.objenesis.instantiator.ObjectInstantiator;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface Objenesis {
    public <T> T newInstance(Class<T> var1);

    public <T> ObjectInstantiator<T> getInstantiatorOf(Class<T> var1);
}

/*
 * Decompiled with CFR 0.152.
 */
package org.objenesis;

import java.util.concurrent.ConcurrentHashMap;
import org.objenesis.Objenesis;
import org.objenesis.instantiator.ObjectInstantiator;
import org.objenesis.strategy.InstantiatorStrategy;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class ObjenesisBase
implements Objenesis {
    protected final InstantiatorStrategy strategy;
    protected ConcurrentHashMap<String, ObjectInstantiator<?>> cache;

    public ObjenesisBase(InstantiatorStrategy strategy) {
        this(strategy, true);
    }

    public ObjenesisBase(InstantiatorStrategy strategy, boolean useCache) {
        if (strategy == null) {
            throw new IllegalArgumentException("A strategy can't be null");
        }
        this.strategy = strategy;
        this.cache = useCache ? new ConcurrentHashMap() : null;
    }

    public String toString() {
        return this.getClass().getName() + " using " + this.strategy.getClass().getName() + (this.cache == null ? " without" : " with") + " caching";
    }

    @Override
    public <T> T newInstance(Class<T> clazz) {
        return this.getInstantiatorOf(clazz).newInstance();
    }

    @Override
    public <T> ObjectInstantiator<T> getInstantiatorOf(Class<T> clazz) {
        if (this.cache == null) {
            return this.strategy.newInstantiatorOf(clazz);
        }
        ObjectInstantiator<Object> instantiator = this.cache.get(clazz.getName());
        if (instantiator == null) {
            ObjectInstantiator<T> newInstantiator = this.strategy.newInstantiatorOf(clazz);
            instantiator = this.cache.putIfAbsent(clazz.getName(), newInstantiator);
            if (instantiator == null) {
                instantiator = newInstantiator;
            }
        }
        return instantiator;
    }
}

/*
 * Decompiled with CFR 0.152.
 */
package org.objenesis;

public class ObjenesisException
extends RuntimeException {
    private static final long serialVersionUID = -2677230016262426968L;

    public ObjenesisException(String msg) {
        super(msg);
    }

    public ObjenesisException(Throwable cause) {
        super(cause);
    }

    public ObjenesisException(String msg, Throwable cause) {
        super(msg, cause);
    }
}

/*
 * Decompiled with CFR 0.152.
 */
package org.objenesis;

import java.io.Serializable;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisSerializer;
import org.objenesis.ObjenesisStd;
import org.objenesis.instantiator.ObjectInstantiator;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class ObjenesisHelper {
    private static final Objenesis OBJENESIS_STD = new ObjenesisStd();
    private static final Objenesis OBJENESIS_SERIALIZER = new ObjenesisSerializer();

    private ObjenesisHelper() {
    }

    public static <T> T newInstance(Class<T> clazz) {
        return OBJENESIS_STD.newInstance(clazz);
    }

    public static <T extends Serializable> T newSerializableInstance(Class<T> clazz) {
        return (T)((Serializable)OBJENESIS_SERIALIZER.newInstance(clazz));
    }

    public static <T> ObjectInstantiator<T> getInstantiatorOf(Class<T> clazz) {
        return OBJENESIS_STD.getInstantiatorOf(clazz);
    }

    public static <T extends Serializable> ObjectInstantiator<T> getSerializableObjectInstantiatorOf(Class<T> clazz) {
        return OBJENESIS_SERIALIZER.getInstantiatorOf(clazz);
    }
}

/*
 * Decompiled with CFR 0.152.
 */
package org.objenesis;

import org.objenesis.ObjenesisBase;
import org.objenesis.strategy.SerializingInstantiatorStrategy;

public class ObjenesisSerializer
extends ObjenesisBase {
    public ObjenesisSerializer() {
        super(new SerializingInstantiatorStrategy());
    }

    public ObjenesisSerializer(boolean useCache) {
        super(new SerializingInstantiatorStrategy(), useCache);
    }
}

/*
 * Decompiled with CFR 0.152.
 */
package org.objenesis;

import org.objenesis.ObjenesisBase;
import org.objenesis.strategy.StdInstantiatorStrategy;

public class ObjenesisStd
extends ObjenesisBase {
    public ObjenesisStd() {
        super(new StdInstantiatorStrategy());
    }

    public ObjenesisStd(boolean useCache) {
        super(new StdInstantiatorStrategy(), useCache);
    }
}

/*
 * Decompiled with CFR 0.152.
 */
package org.objenesis.instantiator;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface ObjectInstantiator<T> {
    public T newInstance();
}

/*
 * Decompiled with CFR 0.152.
 */
package org.objenesis.instantiator;

import java.io.Serializable;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class SerializationInstantiatorHelper {
    public static <T> Class<? super T> getNonSerializableSuperClass(Class<T> type) {
        Class<T> result = type;
        while (Serializable.class.isAssignableFrom(result)) {
            if ((result = result.getSuperclass()) != null) continue;
            throw new Error("Bad class hierarchy: No non-serializable parents");
        }
        return result;
    }
}

/*
 * Decompiled with CFR 0.152.
 */
package org.objenesis.instantiator.android;

import java.io.ObjectInputStream;
import java.lang.reflect.Method;
import org.objenesis.ObjenesisException;
import org.objenesis.instantiator.ObjectInstantiator;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class Android10Instantiator<T>
implements ObjectInstantiator<T> {
    private final Class<T> type;
    private final Method newStaticMethod;

    public Android10Instantiator(Class<T> type) {
        this.type = type;
        this.newStaticMethod = Android10Instantiator.getNewStaticMethod();
    }

    @Override
    public T newInstance() {
        try {
            return this.type.cast(this.newStaticMethod.invoke(null, this.type, Object.class));
        }
        catch (Exception e) {
            throw new ObjenesisException(e);
        }
    }

    private static Method getNewStaticMethod() {
        try {
            Method newStaticMethod = ObjectInputStream.class.getDeclaredMethod("newInstance", Class.class, Class.class);
            newStaticMethod.setAccessible(true);
            return newStaticMethod;
        }
        catch (RuntimeException e) {
            throw new ObjenesisException(e);
        }
        catch (NoSuchMethodException e) {
            throw new ObjenesisException(e);
        }
    }
}

/*
 * Decompiled with CFR 0.152.
 */
package org.objenesis.instantiator.android;

import java.io.ObjectStreamClass;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.objenesis.ObjenesisException;
import org.objenesis.instantiator.ObjectInstantiator;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class Android17Instantiator<T>
implements ObjectInstantiator<T> {
    private final Class<T> type;
    private final Method newInstanceMethod;
    private final Integer objectConstructorId;

    public Android17Instantiator(Class<T> type) {
        this.type = type;
        this.newInstanceMethod = Android17Instantiator.getNewInstanceMethod();
        this.objectConstructorId = Android17Instantiator.findConstructorIdForJavaLangObjectConstructor();
    }

    @Override
    public T newInstance() {
        try {
            return this.type.cast(this.newInstanceMethod.invoke(null, this.type, this.objectConstructorId));
        }
        catch (Exception e) {
            throw new ObjenesisException(e);
        }
    }

    private static Method getNewInstanceMethod() {
        try {
            Method newInstanceMethod = ObjectStreamClass.class.getDeclaredMethod("newInstance", Class.class, Integer.TYPE);
            newInstanceMethod.setAccessible(true);
            return newInstanceMethod;
        }
        catch (RuntimeException e) {
            throw new ObjenesisException(e);
        }
        catch (NoSuchMethodException e) {
            throw new ObjenesisException(e);
        }
    }

    private static Integer findConstructorIdForJavaLangObjectConstructor() {
        try {
            Method newInstanceMethod = ObjectStreamClass.class.getDeclaredMethod("getConstructorId", Class.class);
            newInstanceMethod.setAccessible(true);
            return (Integer)newInstanceMethod.invoke(null, Object.class);
        }
        catch (RuntimeException e) {
            throw new ObjenesisException(e);
        }
        catch (NoSuchMethodException e) {
            throw new ObjenesisException(e);
        }
        catch (IllegalAccessException e) {
            throw new ObjenesisException(e);
        }
        catch (InvocationTargetException e) {
            throw new ObjenesisException(e);
        }
    }
}

/*
 * Decompiled with CFR 0.152.
 */
package org.objenesis.instantiator.android;

import java.io.ObjectStreamClass;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.objenesis.ObjenesisException;
import org.objenesis.instantiator.ObjectInstantiator;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class Android18Instantiator<T>
implements ObjectInstantiator<T> {
    private final Class<T> type;
    private final Method newInstanceMethod;
    private final Long objectConstructorId;

    public Android18Instantiator(Class<T> type) {
        this.type = type;
        this.newInstanceMethod = Android18Instantiator.getNewInstanceMethod();
        this.objectConstructorId = Android18Instantiator.findConstructorIdForJavaLangObjectConstructor();
    }

    @Override
    public T newInstance() {
        try {
            return this.type.cast(this.newInstanceMethod.invoke(null, this.type, this.objectConstructorId));
        }
        catch (Exception e) {
            throw new ObjenesisException(e);
        }
    }

    private static Method getNewInstanceMethod() {
        try {
            Method newInstanceMethod = ObjectStreamClass.class.getDeclaredMethod("newInstance", Class.class, Long.TYPE);
            newInstanceMethod.setAccessible(true);
            return newInstanceMethod;
        }
        catch (RuntimeException e) {
            throw new ObjenesisException(e);
        }
        catch (NoSuchMethodException e) {
            throw new ObjenesisException(e);
        }
    }

    private static Long findConstructorIdForJavaLangObjectConstructor() {
        try {
            Method newInstanceMethod = ObjectStreamClass.class.getDeclaredMethod("getConstructorId", Class.class);
            newInstanceMethod.setAccessible(true);
            return (Long)newInstanceMethod.invoke(null, Object.class);
        }
        catch (RuntimeException e) {
            throw new ObjenesisException(e);
        }
        catch (NoSuchMethodException e) {
            throw new ObjenesisException(e);
        }
        catch (IllegalAccessException e) {
            throw new ObjenesisException(e);
        }
        catch (InvocationTargetException e) {
            throw new ObjenesisException(e);
        }
    }
}

/*
 * Decompiled with CFR 0.152.
 */
package org.objenesis.instantiator.android;

import java.io.ObjectStreamClass;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.objenesis.ObjenesisException;
import org.objenesis.instantiator.ObjectInstantiator;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class AndroidSerializationInstantiator<T>
implements ObjectInstantiator<T> {
    private final Class<T> type;
    private final ObjectStreamClass objectStreamClass;
    private final Method newInstanceMethod;

    public AndroidSerializationInstantiator(Class<T> type) {
        this.type = type;
        this.newInstanceMethod = AndroidSerializationInstantiator.getNewInstanceMethod();
        Method m = null;
        try {
            m = ObjectStreamClass.class.getMethod("lookupAny", Class.class);
        }
        catch (NoSuchMethodException e) {
            throw new ObjenesisException(e);
        }
        try {
            this.objectStreamClass = (ObjectStreamClass)m.invoke(null, type);
        }
        catch (IllegalAccessException e) {
            throw new ObjenesisException(e);
        }
        catch (InvocationTargetException e) {
            throw new ObjenesisException(e);
        }
    }

    @Override
    public T newInstance() {
        try {
            return this.type.cast(this.newInstanceMethod.invoke(this.objectStreamClass, this.type));
        }
        catch (IllegalAccessException e) {
            throw new ObjenesisException(e);
        }
        catch (IllegalArgumentException e) {
            throw new ObjenesisException(e);
        }
        catch (InvocationTargetException e) {
            throw new ObjenesisException(e);
        }
    }

    private static Method getNewInstanceMethod() {
        try {
            Method newInstanceMethod = ObjectStreamClass.class.getDeclaredMethod("newInstance", Class.class);
            newInstanceMethod.setAccessible(true);
            return newInstanceMethod;
        }
        catch (RuntimeException e) {
            throw new ObjenesisException(e);
        }
        catch (NoSuchMethodException e) {
            throw new ObjenesisException(e);
        }
    }
}

/*
 * Decompiled with CFR 0.152.
 */
package org.objenesis.instantiator.basic;

import org.objenesis.instantiator.basic.ConstructorInstantiator;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class AccessibleInstantiator<T>
extends ConstructorInstantiator<T> {
    public AccessibleInstantiator(Class<T> type) {
        super(type);
        if (this.constructor != null) {
            this.constructor.setAccessible(true);
        }
    }
}

/*
 * Decompiled with CFR 0.152.
 */
package org.objenesis.instantiator.basic;

import java.lang.reflect.Constructor;
import org.objenesis.ObjenesisException;
import org.objenesis.instantiator.ObjectInstantiator;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class ConstructorInstantiator<T>
implements ObjectInstantiator<T> {
    protected Constructor<T> constructor;

    public ConstructorInstantiator(Class<T> type) {
        try {
            this.constructor = type.getDeclaredConstructor(null);
        }
        catch (Exception e) {
            throw new ObjenesisException(e);
        }
    }

    @Override
    public T newInstance() {
        try {
            return this.constructor.newInstance(null);
        }
        catch (Exception e) {
            throw new ObjenesisException(e);
        }
    }
}

/*
 * Decompiled with CFR 0.152.
 */
package org.objenesis.instantiator.basic;

import org.objenesis.ObjenesisException;
import org.objenesis.instantiator.ObjectInstantiator;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class FailingInstantiator<T>
implements ObjectInstantiator<T> {
    public FailingInstantiator(Class<T> type) {
    }

    @Override
    public T newInstance() {
        throw new ObjenesisException("Always failing");
    }
}

/*
 * Decompiled with CFR 0.152.
 */
package org.objenesis.instantiator.basic;

import org.objenesis.ObjenesisException;
import org.objenesis.instantiator.ObjectInstantiator;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class NewInstanceInstantiator<T>
implements ObjectInstantiator<T> {
    private final Class<T> type;

    public NewInstanceInstantiator(Class<T> type) {
        this.type = type;
    }

    @Override
    public T newInstance() {
        try {
            return this.type.newInstance();
        }
        catch (Exception e) {
            throw new ObjenesisException(e);
        }
    }
}

/*
 * Decompiled with CFR 0.152.
 */
package org.objenesis.instantiator.basic;

import org.objenesis.instantiator.ObjectInstantiator;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class NullInstantiator<T>
implements ObjectInstantiator<T> {
    public NullInstantiator(Class<T> type) {
    }

    @Override
    public T newInstance() {
        return null;
    }
}

/*
 * Decompiled with CFR 0.152.
 */
package org.objenesis.instantiator.basic;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.io.Serializable;
import org.objenesis.ObjenesisException;
import org.objenesis.instantiator.ObjectInstantiator;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class ObjectInputStreamInstantiator<T>
implements ObjectInstantiator<T> {
    private ObjectInputStream inputStream;

    public ObjectInputStreamInstantiator(Class<T> clazz) {
        if (Serializable.class.isAssignableFrom(clazz)) {
            try {
                this.inputStream = new ObjectInputStream(new MockStream(clazz));
            }
            catch (IOException e) {
                throw new Error("IOException: " + e.getMessage());
            }
        } else {
            throw new ObjenesisException(new NotSerializableException(clazz + " not serializable"));
        }
    }

    @Override
    public T newInstance() {
        try {
            return (T)this.inputStream.readObject();
        }
        catch (ClassNotFoundException e) {
            throw new Error("ClassNotFoundException: " + e.getMessage());
        }
        catch (Exception e) {
            throw new ObjenesisException(e);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class MockStream
    extends InputStream {
        private int pointer = 0;
        private byte[] data = HEADER;
        private int sequence = 0;
        private static final int[] NEXT = new int[]{1, 2, 2};
        private byte[][] buffers;
        private final byte[] FIRST_DATA;
        private static byte[] HEADER;
        private static byte[] REPEATING_DATA;

        private static void initialize() {
            try {
                ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
                DataOutputStream dout = new DataOutputStream(byteOut);
                dout.writeShort(-21267);
                dout.writeShort(5);
                HEADER = byteOut.toByteArray();
                byteOut = new ByteArrayOutputStream();
                dout = new DataOutputStream(byteOut);
                dout.writeByte(115);
                dout.writeByte(113);
                dout.writeInt(0x7E0000);
                REPEATING_DATA = byteOut.toByteArray();
            }
            catch (IOException e) {
                throw new Error("IOException: " + e.getMessage());
            }
        }

        public MockStream(Class<?> clazz) {
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            DataOutputStream dout = new DataOutputStream(byteOut);
            try {
                dout.writeByte(115);
                dout.writeByte(114);
                dout.writeUTF(clazz.getName());
                dout.writeLong(ObjectStreamClass.lookup(clazz).getSerialVersionUID());
                dout.writeByte(2);
                dout.writeShort(0);
                dout.writeByte(120);
                dout.writeByte(112);
            }
            catch (IOException e) {
                throw new Error("IOException: " + e.getMessage());
            }
            this.FIRST_DATA = byteOut.toByteArray();
            this.buffers = new byte[][]{HEADER, this.FIRST_DATA, REPEATING_DATA};
        }

        private void advanceBuffer() {
            this.pointer = 0;
            this.sequence = NEXT[this.sequence];
            this.data = this.buffers[this.sequence];
        }

        @Override
        public int read() throws IOException {
            byte result = this.data[this.pointer++];
            if (this.pointer >= this.data.length) {
                this.advanceBuffer();
            }
            return result;
        }

        @Override
        public int available() throws IOException {
            return Integer.MAX_VALUE;
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            int left;
            int remaining = this.data.length - this.pointer;
            for (left = len; remaining <= left; left -= remaining) {
                System.arraycopy(this.data, this.pointer, b, off, remaining);
                off += remaining;
                this.advanceBuffer();
                remaining = this.data.length - this.pointer;
            }
            if (left > 0) {
                System.arraycopy(this.data, this.pointer, b, off, left);
                this.pointer += left;
            }
            return len;
        }

        static {
            MockStream.initialize();
        }
    }
}

/*
 * Decompiled with CFR 0.152.
 */
package org.objenesis.instantiator.basic;

import java.io.ObjectStreamClass;
import java.lang.reflect.Method;
import org.objenesis.ObjenesisException;
import org.objenesis.instantiator.ObjectInstantiator;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class ObjectStreamClassInstantiator<T>
implements ObjectInstantiator<T> {
    private static Method newInstanceMethod;
    private final ObjectStreamClass objStreamClass;

    private static void initialize() {
        if (newInstanceMethod == null) {
            try {
                newInstanceMethod = ObjectStreamClass.class.getDeclaredMethod("newInstance", new Class[0]);
                newInstanceMethod.setAccessible(true);
            }
            catch (RuntimeException e) {
                throw new ObjenesisException(e);
            }
            catch (NoSuchMethodException e) {
                throw new ObjenesisException(e);
            }
        }
    }

    public ObjectStreamClassInstantiator(Class<T> type) {
        ObjectStreamClassInstantiator.initialize();
        this.objStreamClass = ObjectStreamClass.lookup(type);
    }

    @Override
    public T newInstance() {
        try {
            return (T)newInstanceMethod.invoke(this.objStreamClass, new Object[0]);
        }
        catch (Exception e) {
            throw new ObjenesisException(e);
        }
    }
}

/*
 * Decompiled with CFR 0.152.
 */
package org.objenesis.instantiator.gcj;

import java.lang.reflect.InvocationTargetException;
import org.objenesis.ObjenesisException;
import org.objenesis.instantiator.gcj.GCJInstantiatorBase;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class GCJInstantiator<T>
extends GCJInstantiatorBase<T> {
    public GCJInstantiator(Class<T> type) {
        super(type);
    }

    @Override
    public T newInstance() {
        try {
            return this.type.cast(newObjectMethod.invoke(dummyStream, this.type, Object.class));
        }
        catch (RuntimeException e) {
            throw new ObjenesisException(e);
        }
        catch (IllegalAccessException e) {
            throw new ObjenesisException(e);
        }
        catch (InvocationTargetException e) {
            throw new ObjenesisException(e);
        }
    }
}

/*
 * Decompiled with CFR 0.152.
 */
package org.objenesis.instantiator.gcj;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.reflect.Method;
import org.objenesis.ObjenesisException;
import org.objenesis.instantiator.ObjectInstantiator;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class GCJInstantiatorBase<T>
implements ObjectInstantiator<T> {
    static Method newObjectMethod = null;
    static ObjectInputStream dummyStream;
    protected final Class<T> type;

    private static void initialize() {
        if (newObjectMethod == null) {
            try {
                newObjectMethod = ObjectInputStream.class.getDeclaredMethod("newObject", Class.class, Class.class);
                newObjectMethod.setAccessible(true);
                dummyStream = new DummyStream();
            }
            catch (RuntimeException e) {
                throw new ObjenesisException(e);
            }
            catch (NoSuchMethodException e) {
                throw new ObjenesisException(e);
            }
            catch (IOException e) {
                throw new ObjenesisException(e);
            }
        }
    }

    public GCJInstantiatorBase(Class<T> type) {
        this.type = type;
        GCJInstantiatorBase.initialize();
    }

    @Override
    public abstract T newInstance();

    private static class DummyStream
    extends ObjectInputStream {
    }
}

/*
 * Decompiled with CFR 0.152.
 */
package org.objenesis.instantiator.gcj;

import org.objenesis.ObjenesisException;
import org.objenesis.instantiator.SerializationInstantiatorHelper;
import org.objenesis.instantiator.gcj.GCJInstantiatorBase;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class GCJSerializationInstantiator<T>
extends GCJInstantiatorBase<T> {
    private Class<? super T> superType;

    public GCJSerializationInstantiator(Class<T> type) {
        super(type);
        this.superType = SerializationInstantiatorHelper.getNonSerializableSuperClass(type);
    }

    @Override
    public T newInstance() {
        try {
            return this.type.cast(newObjectMethod.invoke(dummyStream, this.type, this.superType));
        }
        catch (Exception e) {
            throw new ObjenesisException(e);
        }
    }
}

/*
 * Decompiled with CFR 0.152.
 */
package org.objenesis.instantiator.jrockit;

import java.lang.reflect.Method;
import org.objenesis.ObjenesisException;
import org.objenesis.instantiator.ObjectInstantiator;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class JRockitLegacyInstantiator<T>
implements ObjectInstantiator<T> {
    private static Method safeAllocObjectMethod = null;
    private final Class<T> type;

    private static void initialize() {
        if (safeAllocObjectMethod == null) {
            try {
                Class<?> memSystem = Class.forName("jrockit.vm.MemSystem");
                safeAllocObjectMethod = memSystem.getDeclaredMethod("safeAllocObject", Class.class);
                safeAllocObjectMethod.setAccessible(true);
            }
            catch (RuntimeException e) {
                throw new ObjenesisException(e);
            }
            catch (ClassNotFoundException e) {
                throw new ObjenesisException(e);
            }
            catch (NoSuchMethodException e) {
                throw new ObjenesisException(e);
            }
        }
    }

    public JRockitLegacyInstantiator(Class<T> type) {
        JRockitLegacyInstantiator.initialize();
        this.type = type;
    }

    @Override
    public T newInstance() {
        try {
            return this.type.cast(safeAllocObjectMethod.invoke(null, this.type));
        }
        catch (Exception e) {
            throw new ObjenesisException(e);
        }
    }
}

/*
 * Decompiled with CFR 0.152.
 */
package org.objenesis.instantiator.perc;

import java.io.ObjectInputStream;
import java.lang.reflect.Method;
import org.objenesis.ObjenesisException;
import org.objenesis.instantiator.ObjectInstantiator;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class PercInstantiator<T>
implements ObjectInstantiator<T> {
    private final Method newInstanceMethod;
    private final Object[] typeArgs = new Object[]{null, Boolean.FALSE};

    public PercInstantiator(Class<T> type) {
        this.typeArgs[0] = type;
        try {
            this.newInstanceMethod = ObjectInputStream.class.getDeclaredMethod("newInstance", Class.class, Boolean.TYPE);
            this.newInstanceMethod.setAccessible(true);
        }
        catch (RuntimeException e) {
            throw new ObjenesisException(e);
        }
        catch (NoSuchMethodException e) {
            throw new ObjenesisException(e);
        }
    }

    @Override
    public T newInstance() {
        try {
            return (T)this.newInstanceMethod.invoke(null, this.typeArgs);
        }
        catch (Exception e) {
            throw new ObjenesisException(e);
        }
    }
}

/*
 * Decompiled with CFR 0.152.
 */
package org.objenesis.instantiator.perc;

import java.io.ObjectInputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.objenesis.ObjenesisException;
import org.objenesis.instantiator.ObjectInstantiator;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class PercSerializationInstantiator<T>
implements ObjectInstantiator<T> {
    private Object[] typeArgs;
    private final Method newInstanceMethod;

    public PercSerializationInstantiator(Class<T> type) {
        Class<T> unserializableType = type;
        while (Serializable.class.isAssignableFrom(unserializableType)) {
            unserializableType = unserializableType.getSuperclass();
        }
        try {
            Class<?> percMethodClass = Class.forName("COM.newmonics.PercClassLoader.Method");
            this.newInstanceMethod = ObjectInputStream.class.getDeclaredMethod("noArgConstruct", Class.class, Object.class, percMethodClass);
            this.newInstanceMethod.setAccessible(true);
            Class<?> percClassClass = Class.forName("COM.newmonics.PercClassLoader.PercClass");
            Method getPercClassMethod = percClassClass.getDeclaredMethod("getPercClass", Class.class);
            Object someObject = getPercClassMethod.invoke(null, unserializableType);
            Method findMethodMethod = someObject.getClass().getDeclaredMethod("findMethod", String.class);
            Object percMethod = findMethodMethod.invoke(someObject, "<init>()V");
            this.typeArgs = new Object[]{unserializableType, type, percMethod};
        }
        catch (ClassNotFoundException e) {
            throw new ObjenesisException(e);
        }
        catch (NoSuchMethodException e) {
            throw new ObjenesisException(e);
        }
        catch (InvocationTargetException e) {
            throw new ObjenesisException(e);
        }
        catch (IllegalAccessException e) {
            throw new ObjenesisException(e);
        }
    }

    @Override
    public T newInstance() {
        try {
            return (T)this.newInstanceMethod.invoke(null, this.typeArgs);
        }
        catch (IllegalAccessException e) {
            throw new ObjenesisException(e);
        }
        catch (InvocationTargetException e) {
            throw new ObjenesisException(e);
        }
    }
}

/*
 * Decompiled with CFR 0.152.
 */
package org.objenesis.instantiator.sun;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.objenesis.ObjenesisException;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
class SunReflectionFactoryHelper {
    SunReflectionFactoryHelper() {
    }

    public static <T> Constructor<T> newConstructorForSerialization(Class<T> type, Constructor<?> constructor) {
        Class<?> reflectionFactoryClass = SunReflectionFactoryHelper.getReflectionFactoryClass();
        Object reflectionFactory = SunReflectionFactoryHelper.createReflectionFactory(reflectionFactoryClass);
        Method newConstructorForSerializationMethod = SunReflectionFactoryHelper.getNewConstructorForSerializationMethod(reflectionFactoryClass);
        try {
            return (Constructor)newConstructorForSerializationMethod.invoke(reflectionFactory, type, constructor);
        }
        catch (IllegalArgumentException e) {
            throw new ObjenesisException(e);
        }
        catch (IllegalAccessException e) {
            throw new ObjenesisException(e);
        }
        catch (InvocationTargetException e) {
            throw new ObjenesisException(e);
        }
    }

    private static Class<?> getReflectionFactoryClass() {
        try {
            return Class.forName("sun.reflect.ReflectionFactory");
        }
        catch (ClassNotFoundException e) {
            throw new ObjenesisException(e);
        }
    }

    private static Object createReflectionFactory(Class<?> reflectionFactoryClass) {
        try {
            Method method = reflectionFactoryClass.getDeclaredMethod("getReflectionFactory", new Class[0]);
            return method.invoke(null, new Object[0]);
        }
        catch (NoSuchMethodException e) {
            throw new ObjenesisException(e);
        }
        catch (IllegalAccessException e) {
            throw new ObjenesisException(e);
        }
        catch (IllegalArgumentException e) {
            throw new ObjenesisException(e);
        }
        catch (InvocationTargetException e) {
            throw new ObjenesisException(e);
        }
    }

    private static Method getNewConstructorForSerializationMethod(Class<?> reflectionFactoryClass) {
        try {
            return reflectionFactoryClass.getDeclaredMethod("newConstructorForSerialization", Class.class, Constructor.class);
        }
        catch (NoSuchMethodException e) {
            throw new ObjenesisException(e);
        }
    }
}

/*
 * Decompiled with CFR 0.152.
 */
package org.objenesis.instantiator.sun;

import java.lang.reflect.Constructor;
import org.objenesis.ObjenesisException;
import org.objenesis.instantiator.ObjectInstantiator;
import org.objenesis.instantiator.sun.SunReflectionFactoryHelper;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class SunReflectionFactoryInstantiator<T>
implements ObjectInstantiator<T> {
    private final Constructor<T> mungedConstructor;

    public SunReflectionFactoryInstantiator(Class<T> type) {
        Constructor<Object> javaLangObjectConstructor = SunReflectionFactoryInstantiator.getJavaLangObjectConstructor();
        this.mungedConstructor = SunReflectionFactoryHelper.newConstructorForSerialization(type, javaLangObjectConstructor);
        this.mungedConstructor.setAccessible(true);
    }

    @Override
    public T newInstance() {
        try {
            return this.mungedConstructor.newInstance(null);
        }
        catch (Exception e) {
            throw new ObjenesisException(e);
        }
    }

    private static Constructor<Object> getJavaLangObjectConstructor() {
        try {
            return Object.class.getConstructor(null);
        }
        catch (NoSuchMethodException e) {
            throw new ObjenesisException(e);
        }
    }
}

/*
 * Decompiled with CFR 0.152.
 */
package org.objenesis.instantiator.sun;

import java.io.NotSerializableException;
import java.lang.reflect.Constructor;
import org.objenesis.ObjenesisException;
import org.objenesis.instantiator.ObjectInstantiator;
import org.objenesis.instantiator.SerializationInstantiatorHelper;
import org.objenesis.instantiator.sun.SunReflectionFactoryHelper;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class SunReflectionFactorySerializationInstantiator<T>
implements ObjectInstantiator<T> {
    private final Constructor<T> mungedConstructor;

    public SunReflectionFactorySerializationInstantiator(Class<T> type) {
        Constructor<T> nonSerializableAncestorConstructor;
        Class<T> nonSerializableAncestor = SerializationInstantiatorHelper.getNonSerializableSuperClass(type);
        try {
            nonSerializableAncestorConstructor = nonSerializableAncestor.getConstructor(null);
        }
        catch (NoSuchMethodException e) {
            throw new ObjenesisException(new NotSerializableException(type + " has no suitable superclass constructor"));
        }
        this.mungedConstructor = SunReflectionFactoryHelper.newConstructorForSerialization(type, nonSerializableAncestorConstructor);
        this.mungedConstructor.setAccessible(true);
    }

    @Override
    public T newInstance() {
        try {
            return this.mungedConstructor.newInstance(null);
        }
        catch (Exception e) {
            throw new ObjenesisException(e);
        }
    }
}

/*
 * Decompiled with CFR 0.152.
 */
package org.objenesis.instantiator.sun;

import java.lang.reflect.Field;
import org.objenesis.ObjenesisException;
import org.objenesis.instantiator.ObjectInstantiator;
import sun.misc.Unsafe;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class UnsafeFactoryInstantiator<T>
implements ObjectInstantiator<T> {
    private static Unsafe unsafe;
    private final Class<T> type;

    public UnsafeFactoryInstantiator(Class<T> type) {
        if (unsafe == null) {
            Field f;
            try {
                f = Unsafe.class.getDeclaredField("theUnsafe");
            }
            catch (NoSuchFieldException e) {
                throw new ObjenesisException(e);
            }
            f.setAccessible(true);
            try {
                unsafe = (Unsafe)f.get(null);
            }
            catch (IllegalAccessException e) {
                throw new ObjenesisException(e);
            }
        }
        this.type = type;
    }

    @Override
    public T newInstance() {
        try {
            return this.type.cast(unsafe.allocateInstance(this.type));
        }
        catch (InstantiationException e) {
            throw new ObjenesisException(e);
        }
    }
}

/*
 * Decompiled with CFR 0.152.
 */
package org.objenesis.strategy;

import org.objenesis.strategy.InstantiatorStrategy;

public abstract class BaseInstantiatorStrategy
implements InstantiatorStrategy {
}

/*
 * Decompiled with CFR 0.152.
 */
package org.objenesis.strategy;

import org.objenesis.instantiator.ObjectInstantiator;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface InstantiatorStrategy {
    public <T> ObjectInstantiator<T> newInstantiatorOf(Class<T> var1);
}

/*
 * Decompiled with CFR 0.152.
 */
package org.objenesis.strategy;

import java.lang.reflect.Field;
import org.objenesis.ObjenesisException;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class PlatformDescription {
    public static final String JROCKIT = "BEA";
    public static final String GNU = "GNU libgcj";
    public static final String SUN = "Java HotSpot";
    public static final String OPENJDK = "OpenJDK";
    public static final String PERC = "PERC";
    public static final String DALVIK = "Dalvik";
    public static final String SPECIFICATION_VERSION = System.getProperty("java.specification.version");
    public static final String VM_VERSION = System.getProperty("java.runtime.version");
    public static final String VM_INFO = System.getProperty("java.vm.info");
    public static final String VENDOR_VERSION = System.getProperty("java.vm.version");
    public static final String VENDOR = System.getProperty("java.vm.vendor");
    public static final String JVM_NAME = System.getProperty("java.vm.name");
    public static final int ANDROID_VERSION = PlatformDescription.getAndroidVersion();

    public static boolean isThisJVM(String name) {
        return JVM_NAME.startsWith(name);
    }

    private static int getAndroidVersion() {
        if (!PlatformDescription.isThisJVM(DALVIK)) {
            return 0;
        }
        return PlatformDescription.getAndroidVersion0();
    }

    private static int getAndroidVersion0() {
        int version;
        Field field;
        Class<?> clazz;
        try {
            clazz = Class.forName("android.os.Build$VERSION");
        }
        catch (ClassNotFoundException e) {
            throw new ObjenesisException(e);
        }
        try {
            field = clazz.getField("SDK_INT");
        }
        catch (NoSuchFieldException e) {
            return PlatformDescription.getOldAndroidVersion(clazz);
        }
        try {
            version = (Integer)field.get(null);
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return version;
    }

    private static int getOldAndroidVersion(Class<?> versionClass) {
        String version;
        Field field;
        try {
            field = versionClass.getField("SDK");
        }
        catch (NoSuchFieldException e) {
            throw new ObjenesisException(e);
        }
        try {
            version = (String)field.get(null);
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return Integer.parseInt(version);
    }

    private PlatformDescription() {
    }
}

/*
 * Decompiled with CFR 0.152.
 */
package org.objenesis.strategy;

import java.io.NotSerializableException;
import java.io.Serializable;
import org.objenesis.ObjenesisException;
import org.objenesis.instantiator.ObjectInstantiator;
import org.objenesis.instantiator.android.AndroidSerializationInstantiator;
import org.objenesis.instantiator.basic.ObjectStreamClassInstantiator;
import org.objenesis.instantiator.gcj.GCJSerializationInstantiator;
import org.objenesis.instantiator.perc.PercSerializationInstantiator;
import org.objenesis.strategy.BaseInstantiatorStrategy;
import org.objenesis.strategy.PlatformDescription;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class SerializingInstantiatorStrategy
extends BaseInstantiatorStrategy {
    @Override
    public <T> ObjectInstantiator<T> newInstantiatorOf(Class<T> type) {
        if (!Serializable.class.isAssignableFrom(type)) {
            throw new ObjenesisException(new NotSerializableException(type + " not serializable"));
        }
        if (PlatformDescription.JVM_NAME.startsWith("Java HotSpot") || PlatformDescription.isThisJVM("OpenJDK")) {
            return new ObjectStreamClassInstantiator<T>(type);
        }
        if (PlatformDescription.JVM_NAME.startsWith("Dalvik")) {
            return new AndroidSerializationInstantiator<T>(type);
        }
        if (PlatformDescription.JVM_NAME.startsWith("GNU libgcj")) {
            return new GCJSerializationInstantiator<T>(type);
        }
        if (PlatformDescription.JVM_NAME.startsWith("PERC")) {
            return new PercSerializationInstantiator<T>(type);
        }
        return new ObjectStreamClassInstantiator<T>(type);
    }
}

/*
 * Decompiled with CFR 0.152.
 */
package org.objenesis.strategy;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import org.objenesis.ObjenesisException;
import org.objenesis.instantiator.ObjectInstantiator;
import org.objenesis.strategy.InstantiatorStrategy;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class SingleInstantiatorStrategy
implements InstantiatorStrategy {
    private Constructor<?> constructor;

    public <T extends ObjectInstantiator<?>> SingleInstantiatorStrategy(Class<T> instantiator) {
        try {
            this.constructor = instantiator.getConstructor(Class.class);
        }
        catch (NoSuchMethodException e) {
            throw new ObjenesisException(e);
        }
    }

    @Override
    public <T> ObjectInstantiator<T> newInstantiatorOf(Class<T> type) {
        try {
            return (ObjectInstantiator)this.constructor.newInstance(type);
        }
        catch (InstantiationException e) {
            throw new ObjenesisException(e);
        }
        catch (IllegalAccessException e) {
            throw new ObjenesisException(e);
        }
        catch (InvocationTargetException e) {
            throw new ObjenesisException(e);
        }
    }
}

/*
 * Decompiled with CFR 0.152.
 */
package org.objenesis.strategy;

import org.objenesis.instantiator.ObjectInstantiator;
import org.objenesis.instantiator.android.Android10Instantiator;
import org.objenesis.instantiator.android.Android17Instantiator;
import org.objenesis.instantiator.android.Android18Instantiator;
import org.objenesis.instantiator.gcj.GCJInstantiator;
import org.objenesis.instantiator.jrockit.JRockitLegacyInstantiator;
import org.objenesis.instantiator.perc.PercInstantiator;
import org.objenesis.instantiator.sun.SunReflectionFactoryInstantiator;
import org.objenesis.instantiator.sun.UnsafeFactoryInstantiator;
import org.objenesis.strategy.BaseInstantiatorStrategy;
import org.objenesis.strategy.PlatformDescription;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class StdInstantiatorStrategy
extends BaseInstantiatorStrategy {
    @Override
    public <T> ObjectInstantiator<T> newInstantiatorOf(Class<T> type) {
        if (PlatformDescription.isThisJVM("Java HotSpot") || PlatformDescription.isThisJVM("OpenJDK")) {
            return new SunReflectionFactoryInstantiator<T>(type);
        }
        if (PlatformDescription.isThisJVM("BEA")) {
            if (!(!PlatformDescription.VM_VERSION.startsWith("1.4") || PlatformDescription.VENDOR_VERSION.startsWith("R") || PlatformDescription.VM_INFO != null && PlatformDescription.VM_INFO.startsWith("R25.1") && PlatformDescription.VM_INFO.startsWith("R25.2"))) {
                return new JRockitLegacyInstantiator<T>(type);
            }
            return new SunReflectionFactoryInstantiator<T>(type);
        }
        if (PlatformDescription.isThisJVM("Dalvik")) {
            if (PlatformDescription.ANDROID_VERSION <= 10) {
                return new Android10Instantiator<T>(type);
            }
            if (PlatformDescription.ANDROID_VERSION <= 17) {
                return new Android17Instantiator<T>(type);
            }
            return new Android18Instantiator<T>(type);
        }
        if (PlatformDescription.isThisJVM("GNU libgcj")) {
            return new GCJInstantiator<T>(type);
        }
        if (PlatformDescription.isThisJVM("PERC")) {
            return new PercInstantiator<T>(type);
        }
        return new UnsafeFactoryInstantiator<T>(type);
    }
}

/*
 * Decompiled with CFR 0.152.
 */
package net.java.games.util;

public final class Version {
    private static final String version = "1.0.0-b01";

    private Version() {
    }

    public static String getVersion() {
        return version;
    }
}

/*
 * Decompiled with CFR 0.152.
 */
package net.java.games.util.plugins;

public interface Plugin {
}

/*
 * Decompiled with CFR 0.152.
 */
package net.java.games.util.plugins;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class PluginLoader
extends URLClassLoader {
    static final boolean DEBUG = false;
    File parentDir;
    boolean localDLLs = true;
    static /* synthetic */ Class class$net$java$games$util$plugins$Plugin;

    public PluginLoader(File jf) throws MalformedURLException {
        super(new URL[]{jf.toURL()}, Thread.currentThread().getContextClassLoader());
        this.parentDir = jf.getParentFile();
        if (System.getProperty("net.java.games.util.plugins.nolocalnative") != null) {
            this.localDLLs = false;
        }
    }

    protected String findLibrary(String libname) {
        if (this.localDLLs) {
            String libpath = this.parentDir.getPath() + File.separator + System.mapLibraryName(libname);
            return libpath;
        }
        return super.findLibrary(libname);
    }

    public boolean attemptPluginDefine(Class pc) {
        return !pc.isInterface() && this.classImplementsPlugin(pc);
    }

    private boolean classImplementsPlugin(Class testClass) {
        int i;
        if (testClass == null) {
            return false;
        }
        Class<?>[] implementedInterfaces = testClass.getInterfaces();
        for (i = 0; i < implementedInterfaces.length; ++i) {
            if (implementedInterfaces[i] != (class$net$java$games$util$plugins$Plugin == null ? PluginLoader.class$("net.java.games.util.plugins.Plugin") : class$net$java$games$util$plugins$Plugin)) continue;
            return true;
        }
        for (i = 0; i < implementedInterfaces.length; ++i) {
            if (!this.classImplementsPlugin(implementedInterfaces[i])) continue;
            return true;
        }
        return this.classImplementsPlugin(testClass.getSuperclass());
    }
}

/*
 * Decompiled with CFR 0.152.
 */
package net.java.games.util.plugins;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import net.java.games.util.plugins.PluginLoader;

public class Plugins {
    static final boolean DEBUG = true;
    List pluginList = new ArrayList();

    public Plugins(File pluginRoot) throws IOException {
        this.scanPlugins(pluginRoot);
    }

    private void scanPlugins(File dir) throws IOException {
        File[] files = dir.listFiles();
        if (files == null) {
            throw new FileNotFoundException("Plugin directory " + dir.getName() + " not found.");
        }
        for (int i = 0; i < files.length; ++i) {
            File f = files[i];
            if (f.getName().endsWith(".jar")) {
                this.processJar(f);
                continue;
            }
            if (!f.isDirectory()) continue;
            this.scanPlugins(f);
        }
    }

    private void processJar(File f) {
        try {
            System.out.println("Scanning jar: " + f.getName());
            PluginLoader loader = new PluginLoader(f);
            JarFile jf = new JarFile(f);
            Enumeration<JarEntry> en = jf.entries();
            while (en.hasMoreElements()) {
                JarEntry je = en.nextElement();
                System.out.println("Examining file : " + je.getName());
                if (!je.getName().endsWith("Plugin.class")) continue;
                System.out.println("Found candidate class: " + je.getName());
                String cname = je.getName();
                cname = cname.substring(0, cname.length() - 6);
                Class<?> pc = loader.loadClass(cname = cname.replace('/', '.'));
                if (!loader.attemptPluginDefine(pc)) continue;
                System.out.println("Adding class to plugins:" + pc.getName());
                this.pluginList.add(pc);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Class[] get() {
        Class[] pluginArray = new Class[this.pluginList.size()];
        return this.pluginList.toArray(pluginArray);
    }

    public Class[] getImplementsAny(Class[] interfaces) {
        ArrayList<Class> matchList = new ArrayList<Class>(this.pluginList.size());
        HashSet<Class> interfaceSet = new HashSet<Class>();
        for (int i = 0; i < interfaces.length; ++i) {
            interfaceSet.add(interfaces[i]);
        }
        Iterator i = this.pluginList.iterator();
        while (i.hasNext()) {
            Class pluginClass = (Class)i.next();
            if (!this.classImplementsAny(pluginClass, interfaceSet)) continue;
            matchList.add(pluginClass);
        }
        Class[] pluginArray = new Class[matchList.size()];
        return matchList.toArray(pluginArray);
    }

    private boolean classImplementsAny(Class testClass, Set interfaces) {
        int i;
        if (testClass == null) {
            return false;
        }
        Class<?>[] implementedInterfaces = testClass.getInterfaces();
        for (i = 0; i < implementedInterfaces.length; ++i) {
            if (!interfaces.contains(implementedInterfaces[i])) continue;
            return true;
        }
        for (i = 0; i < implementedInterfaces.length; ++i) {
            if (!this.classImplementsAny(implementedInterfaces[i], interfaces)) continue;
            return true;
        }
        return this.classImplementsAny(testClass.getSuperclass(), interfaces);
    }

    public Class[] getImplementsAll(Class[] interfaces) {
        ArrayList<Class> matchList = new ArrayList<Class>(this.pluginList.size());
        HashSet<Class> interfaceSet = new HashSet<Class>();
        for (int i = 0; i < interfaces.length; ++i) {
            interfaceSet.add(interfaces[i]);
        }
        Iterator i = this.pluginList.iterator();
        while (i.hasNext()) {
            Class pluginClass = (Class)i.next();
            if (!this.classImplementsAll(pluginClass, interfaceSet)) continue;
            matchList.add(pluginClass);
        }
        Class[] pluginArray = new Class[matchList.size()];
        return matchList.toArray(pluginArray);
    }

    private boolean classImplementsAll(Class testClass, Set interfaces) {
        int i;
        if (testClass == null) {
            return false;
        }
        Class<?>[] implementedInterfaces = testClass.getInterfaces();
        for (i = 0; i < implementedInterfaces.length; ++i) {
            if (!interfaces.contains(implementedInterfaces[i])) continue;
            interfaces.remove(implementedInterfaces[i]);
            if (interfaces.size() != 0) continue;
            return true;
        }
        for (i = 0; i < implementedInterfaces.length; ++i) {
            if (!this.classImplementsAll(implementedInterfaces[i], interfaces)) continue;
            return true;
        }
        return this.classImplementsAll(testClass.getSuperclass(), interfaces);
    }

    public Class[] getExtends(Class superclass) {
        ArrayList<Class> matchList = new ArrayList<Class>(this.pluginList.size());
        Iterator i = this.pluginList.iterator();
        while (i.hasNext()) {
            Class pluginClass = (Class)i.next();
            if (!this.classExtends(pluginClass, superclass)) continue;
            matchList.add(pluginClass);
        }
        Class[] pluginArray = new Class[matchList.size()];
        return matchList.toArray(pluginArray);
    }

    private boolean classExtends(Class testClass, Class superclass) {
        if (testClass == null) {
            return false;
        }
        if (testClass == superclass) {
            return true;
        }
        return this.classExtends(testClass.getSuperclass(), superclass);
    }
}

/*
 * Decompiled with CFR 0.152.
 */
package com.interrupt.dungeoneer;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.interrupt.api.steam.SteamApi;
import com.interrupt.dungeoneer.GameApplication;
import com.interrupt.dungeoneer.game.Game;
import com.interrupt.dungeoneer.game.Options;
import com.interrupt.dungeoneer.steamapi.SteamDesktopApi;

public class DesktopStarter {
    public static void main(String[] args) {
        if (args != null) {
            for (String arg : args) {
                if (arg.toLowerCase().endsWith("debug=true")) {
                    Game.isDebugMode = true;
                    continue;
                }
                if (arg.toLowerCase().endsWith("debug-collision=true")) {
                    Game.drawDebugBoxes = true;
                    continue;
                }
                if (!arg.toLowerCase().endsWith("version")) continue;
                System.out.println(Game.VERSION);
                System.exit(0);
            }
        }
        Options.loadOptions();
        Graphics.DisplayMode defaultMode = LwjglApplicationConfiguration.getDesktopDisplayMode();
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.title = "Delver";
        config.fullscreen = Options.instance.fullScreen;
        config.width = defaultMode.width;
        config.height = defaultMode.height;
        config.vSyncEnabled = Options.instance.vsyncEnabled;
        config.samples = Options.instance.antiAliasingSamples;
        config.stencil = 8;
        config.foregroundFPS = Options.instance.fpsLimit;
        if (!config.fullscreen) {
            config.width = (int)((double)config.width * 0.8);
            config.height = (int)((double)config.height * 0.8);
        }
        config.audioDeviceBufferCount *= 2;
        config.audioDeviceSimultaneousSources *= 2;
        config.addIcon("icon-128.png", Files.FileType.Internal);
        config.addIcon("icon-32.png", Files.FileType.Internal);
        config.addIcon("icon-16.png", Files.FileType.Internal);
        new LwjglApplication((ApplicationListener)new GameApplication(), config);
        SteamApi.api = new SteamDesktopApi();
        SteamApi.api.init();
    }
}

/*
 * Decompiled with CFR 0.152.
 */
package com.interrupt.dungeoneer.modding;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.interrupt.dungeoneer.game.ModManager;
import com.interrupt.dungeoneer.scripting.ScriptingApi;
import com.interrupt.utils.Logger;
import java.io.File;
import java.io.FilePermission;
import java.lang.reflect.Method;
import java.lang.reflect.ReflectPermission;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessControlContext;
import java.security.Permission;
import java.security.Permissions;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.PropertyPermission;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

public class ScriptLoader
implements ScriptingApi {
    private JavaCompiler compiler;
    private DiagnosticCollector<JavaFileObject> diagnostics;
    private StandardJavaFileManager fileManager;
    private List<String> optionList;
    final String modFolder = "java";

    @Override
    public void loadScripts(ModManager modManager) {
        ArrayMap<FileHandle, FileHandle[]> needsCompiling = modManager.getFilesForModsWithSuffix("java", ".java");
        if (needsCompiling.size > 0) {
            this.compiler = ToolProvider.getSystemJavaCompiler();
            if (this.compiler != null) {
                this.diagnostics = new DiagnosticCollector();
                this.fileManager = this.compiler.getStandardFileManager(this.diagnostics, null, null);
                this.optionList = new ArrayList<String>();
                this.optionList.add("-classpath");
                this.optionList.add(System.getProperty("java.class.path") + ";dist/InlineCompiler.jar");
                this.compileFiles(needsCompiling);
            }
        }
        Array<String> modClassNames = new Array<String>();
        ArrayMap<FileHandle, FileHandle[]> classFiles = modManager.getFilesForModsWithSuffix("java", ".class");
        for (int i = 0; i < classFiles.size; ++i) {
            FileHandle[] mod_classes = classFiles.getValueAt(i);
            for (int ii = 0; ii < mod_classes.length; ++ii) {
                FileHandle classFile = mod_classes[ii];
                String classFilePath = classFile.pathWithoutExtension();
                String className = classFilePath.substring(classFilePath.indexOf("java") + "java".length() + 1);
                modClassNames.add(className.replace('/', '.').replace('\\', '.'));
            }
        }
        if (modClassNames.size > 0) {
            Permissions permissions = new Permissions();
            permissions.add(new FilePermission("<<ALL FILES>>", "read"));
            permissions.add(new PropertyPermission("*", "read"));
            permissions.add(new ReflectPermission("suppressAccessChecks"));
            permissions.add(new RuntimePermission("accessDeclaredMembers"));
            ProtectionDomain protectionDomain = new ProtectionDomain(null, permissions);
            AccessControlContext accessContext = new AccessControlContext(new ProtectionDomain[]{protectionDomain});
            System.setSecurityManager(new SandboxSecurityManager(modClassNames, accessContext));
        }
        Array<FileHandle> classFolders = modManager.getFileInAllMods("java");
        for (int i = 0; i < classFolders.size; ++i) {
            FileHandle dir = classFolders.get(i);
            this.addDirectoryToClassLoader(dir);
        }
        this.compiler = null;
        this.diagnostics = null;
        this.fileManager = null;
        this.optionList = null;
    }

    private void compileFiles(ArrayMap<FileHandle, FileHandle[]> filesByDirectory) {
        for (int i = 0; i < filesByDirectory.size; ++i) {
            try {
                FileHandle dir = filesByDirectory.getKeyAt(i);
                FileHandle[] fileHandles = filesByDirectory.getValueAt(i);
                File[] files = new File[fileHandles.length];
                for (int ii = 0; ii < fileHandles.length; ++ii) {
                    files[ii] = fileHandles[ii].file();
                }
                Iterable<? extends JavaFileObject> compilationUnit = this.fileManager.getJavaFileObjectsFromFiles(Arrays.asList(files));
                JavaCompiler.CompilationTask task = this.compiler.getTask(null, this.fileManager, this.diagnostics, this.optionList, null, compilationUnit);
                if (task.call().booleanValue()) continue;
                for (Diagnostic<JavaFileObject> diagnostic : this.diagnostics.getDiagnostics()) {
                    Logger.logExceptionToFile(new Exception(diagnostic.toString()));
                }
                continue;
            }
            catch (Exception ex) {
                Logger.logExceptionToFile(ex);
            }
        }
    }

    private void addDirectoryToClassLoader(FileHandle directory) {
        try {
            URLClassLoader sysloader = (URLClassLoader)ClassLoader.getSystemClassLoader();
            Class<URLClassLoader> sysclass = URLClassLoader.class;
            URL url = directory.file().toURI().toURL();
            Method method = sysclass.getDeclaredMethod("addURL", URL.class);
            method.setAccessible(true);
            method.invoke(sysloader, url);
        }
        catch (Exception ex) {
            Logger.logExceptionToFile(ex);
        }
    }

    private class SandboxSecurityManager
    extends SecurityManager {
        private final ArrayMap<String, Boolean> checkedClassNames = new ArrayMap();
        private final AccessControlContext sandboxContext;

        SandboxSecurityManager(Array<String> classNamesToRestrict, AccessControlContext sandboxContext) {
            for (String className : classNamesToRestrict) {
                this.checkedClassNames.put(className, true);
            }
            this.sandboxContext = sandboxContext;
        }

        @Override
        public void checkPermission(Permission permission, Object context) {
            this.checkPermission(permission);
        }

        @Override
        public void checkPermission(Permission permission) {
            for (Class<?> clasS : this.getClassContext()) {
                if (!this.checkedClassNames.containsKey(clasS.getName())) continue;
                try {
                    this.sandboxContext.checkPermission(permission);
                }
                catch (SecurityException ex) {
                    Gdx.app.error("Modding", ex.getMessage(), ex);
                    throw ex;
                }
            }
        }
    }
}

/*
 * Decompiled with CFR 0.152.
 */
package com.interrupt.dungeoneer.steamapi;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.codedisaster.steamworks.SteamAPI;
import com.codedisaster.steamworks.SteamException;
import com.codedisaster.steamworks.SteamID;
import com.codedisaster.steamworks.SteamLeaderboardEntriesHandle;
import com.codedisaster.steamworks.SteamLeaderboardHandle;
import com.codedisaster.steamworks.SteamPublishedFileID;
import com.codedisaster.steamworks.SteamResult;
import com.codedisaster.steamworks.SteamUGC;
import com.codedisaster.steamworks.SteamUGCCallback;
import com.codedisaster.steamworks.SteamUGCDetails;
import com.codedisaster.steamworks.SteamUGCQuery;
import com.codedisaster.steamworks.SteamUserStats;
import com.codedisaster.steamworks.SteamUserStatsCallback;
import com.interrupt.api.steam.SteamApiInterface;
import com.interrupt.dungeoneer.game.Game;

public class SteamDesktopApi
implements SteamApiInterface {
    protected SteamUGCCallback ugcCallback = new SteamUGCCallback(){

        @Override
        public void onUGCQueryCompleted(SteamUGCQuery query, int numResultsReturned, int totalMatchingResults, boolean isCachedData, SteamResult result) {
        }

        @Override
        public void onSubscribeItem(SteamPublishedFileID publishedFileID, SteamResult result) {
        }

        @Override
        public void onUnsubscribeItem(SteamPublishedFileID publishedFileID, SteamResult result) {
        }

        @Override
        public void onRequestUGCDetails(SteamUGCDetails details, SteamResult result) {
        }

        @Override
        public void onCreateItem(SteamPublishedFileID publishedFileID, boolean needsToAcceptWLA, SteamResult result) {
        }

        @Override
        public void onSubmitItemUpdate(boolean needsToAcceptWLA, SteamResult result) {
        }

        @Override
        public void onDownloadItemResult(int appID, SteamPublishedFileID publishedFileID, SteamResult result) {
        }

        @Override
        public void onUserFavoriteItemsListChanged(SteamPublishedFileID publishedFileID, boolean wasAddRequest, SteamResult result) {
        }

        @Override
        public void onSetUserItemVote(SteamPublishedFileID publishedFileID, boolean voteUp, SteamResult result) {
        }

        @Override
        public void onGetUserItemVote(SteamPublishedFileID publishedFileID, boolean votedUp, boolean votedDown, boolean voteSkipped, SteamResult result) {
        }

        @Override
        public void onStartPlaytimeTracking(SteamResult result) {
        }

        @Override
        public void onStopPlaytimeTracking(SteamResult result) {
        }

        @Override
        public void onStopPlaytimeTrackingForAllItems(SteamResult result) {
        }
    };
    protected SteamUserStatsCallback userStatsCallback = new SteamUserStatsCallback(){

        @Override
        public void onUserStatsReceived(long gameId, SteamID steamIDUser, SteamResult result) {
        }

        @Override
        public void onUserStatsStored(long gameId, SteamResult result) {
        }

        @Override
        public void onUserStatsUnloaded(SteamID steamIDUser) {
        }

        @Override
        public void onUserAchievementStored(long gameId, boolean isGroupAchievement, String achievementName, int curProgress, int maxProgress) {
        }

        @Override
        public void onLeaderboardFindResult(SteamLeaderboardHandle leaderboard, boolean found) {
        }

        @Override
        public void onLeaderboardScoresDownloaded(SteamLeaderboardHandle leaderboard, SteamLeaderboardEntriesHandle entries, int numEntries) {
        }

        @Override
        public void onLeaderboardScoreUploaded(boolean success, SteamLeaderboardHandle leaderboard, int score, boolean scoreChanged, int globalRankNew, int globalRankPrevious) {
        }

        @Override
        public void onGlobalStatsReceived(long gameId, SteamResult result) {
        }
    };
    protected SteamUserStats stats = null;

    @Override
    public boolean init() {
        try {
            boolean started = SteamAPI.init();
            if (started) {
                this.stats = new SteamUserStats(this.userStatsCallback);
                this.stats.requestCurrentStats();
            }
            return started;
        }
        catch (SteamException e) {
            Gdx.app.error("SteamApi", "Error starting Steam API", e);
            return false;
        }
    }

    @Override
    public Array<String> getWorkshopFolders() {
        Array<String> folders = new Array<String>();
        if (SteamAPI.isSteamRunning()) {
            SteamUGC ugc = new SteamUGC(this.ugcCallback);
            int numSubbed = ugc.getNumSubscribedItems();
            if (numSubbed > 0) {
                SteamPublishedFileID[] ids = new SteamPublishedFileID[numSubbed];
                ugc.getSubscribedItems(ids);
                for (SteamPublishedFileID id : ids) {
                    SteamUGC.ItemInstallInfo installInfo = new SteamUGC.ItemInstallInfo();
                    if (!ugc.getItemInstallInfo(id, installInfo)) continue;
                    Gdx.app.log("SteamApi", "Found Workshop Mod: " + installInfo.getFolder());
                    folders.add(installInfo.getFolder());
                }
                ugc.startPlaytimeTracking(ids);
            } else {
                Gdx.app.log("SteamApi", "No subscribed workshop mods");
            }
        }
        return folders;
    }

    @Override
    public void runCallbacks() {
        if (SteamAPI.isSteamRunning()) {
            SteamAPI.runCallbacks();
        }
    }

    @Override
    public void achieve(String achievementName) {
        if (SteamAPI.isSteamRunning() && this.stats != null) {
            if (this.stats.setAchievement(achievementName)) {
                if (this.stats.storeStats()) {
                    Gdx.app.log("SteamApi", "Set achievement: " + achievementName);
                } else {
                    Gdx.app.log("SteamApi", "Could not store Steam stats");
                }
            } else {
                Gdx.app.log("SteamApi", "Could not set achievement: " + achievementName);
            }
        }
    }

    @Override
    public void achieve(String achievementName, int numProgress, int maxProgress) {
        if (Game.isDebugMode) {
            Gdx.app.log("SteamApi", "No achievements for you!");
            return;
        }
        if (SteamAPI.isSteamRunning() && this.stats != null) {
            if (this.stats.indicateAchievementProgress(achievementName, 1, 1)) {
                if (this.stats.storeStats()) {
                    Gdx.app.log("SteamApi", "Updated achievement: " + achievementName);
                } else {
                    Gdx.app.log("SteamApi", "Could not store Steam stats");
                }
            } else {
                Gdx.app.log("SteamApi", "Could not set achievement: " + achievementName);
            }
        }
    }

    @Override
    public void dispose() {
        if (SteamAPI.isSteamRunning()) {
            SteamUGC ugc = new SteamUGC(this.ugcCallback);
            ugc.stopPlaytimeTrackingForAllItems();
            SteamAPI.shutdown();
        }
    }

    @Override
    public void uploadToWorkshop(Long workshopId, String modImagePath, String modTitle, String modFolderPath) {
    }

    @Override
    public boolean isAvailable() {
        return SteamAPI.isSteamRunning();
    }
}


D:\SteamLibrary\steamapps\common\Delver - copia>