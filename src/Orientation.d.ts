import { NativeModules } from 'react-native';

const { RnOrientation } = NativeModules;

interface OrientationInterface {
  getOrientation(cb: any): void;
  enableScreenOrientation(): void;
  disableScreenOrientation(): void;
}

export default RnOrientation as OrientationInterface;
