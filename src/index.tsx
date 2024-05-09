import { NativeEventEmitter, NativeModules, Platform } from 'react-native';

const ORIENTATION_DID_UPDATE = 'orientationDidUpdate';

let isOrientationLocked = false;
const LINKING_ERROR =
  `The package 'react-native-otp-verify' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: '- You have run \'pod install\'\n', default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo managed workflow\n';
const RnOrientationNative = NativeModules.RnOrientation
  ? NativeModules.RnOrientation
  : new Proxy(
    {},
    {
      get() {
        throw new Error(LINKING_ERROR);
      },
    },
  );
const eventEmitter = new NativeEventEmitter(RnOrientationNative);


export function multiply(a: number, b: number): Promise<number> {
  return RnOrientationNative.multiply(a, b);
}

export default class Orientation {
  static isLocked = () => {
    return isOrientationLocked;
  };

  static getOrientation = (cb: any) => {
    return RnOrientationNative.getOrientation((orientation: string) => {
      cb(orientation);
    });
  };

  static getInitialOrientation = () => {
    const { initialOrientation } = RnOrientationNative.getConstants();
    return initialOrientation;
  };

  static requestEnableOrientations = () => {
    isOrientationLocked = false;
    return RnOrientationNative.enableScreenOrientation();
  };

  static getSendEvent = () => {
    return RnOrientationNative.sendEvent();
  };

  static addOrientationListener = (cb: any) => {
    return eventEmitter.addListener(ORIENTATION_DID_UPDATE, (body) => {
      cb(body.orientation);
    });
  };

  static removeOrientationListener = () => {
    return eventEmitter.removeAllListeners(ORIENTATION_DID_UPDATE);
  };

}
