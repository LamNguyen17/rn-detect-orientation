import { useEffect, useState } from 'react';
import { StyleSheet, View, Text, Button } from 'react-native';
import Orientation, { multiply } from 'rn-detect-orientation';
import { useDeviceOrientationChange } from './useDeviceOrientationChange';

export default function App() {
  const [result, setResult] = useState<number | undefined>();
  const [orient, setOrient] = useState<string>();
  const [isLocked, setLocked] = useState<boolean>(false);
  const [sendValue, setValue] = useState<string | null>(null);

  useDeviceOrientationChange((event) => {
    setOrient(`${event}`);
  });

  useEffect(() => {
    Orientation.addOrientationListener(onOrientationDidUpdate);
    return () => {
      Orientation.removeOrientationListener();
    };
  }, []);

  const onOrientationDidUpdate = (event) => {
    console.log('onOrientationDidUpdate:', event);
    setOrient(`${event}`);
  };

  useEffect(() => {
    let initialOrientation = Orientation.getInitialOrientation();
    console.log('initialOrientation:', initialOrientation);
    setOrient(`${initialOrientation}`);
  }, []);

  const checkLocked = () => {
    const locked = Orientation.isLocked();
    if (locked !== isLocked) {
      setLocked(locked);
    }
  };

  return (
    <View style={styles.container}>
      <Text>Result: {orient}</Text>
      <Button onPress={() => {
        Orientation.requestEnableOrientations();
        checkLocked();
      }}
              title={'Enable Screen Orientation'}>
      </Button>
      <Button onPress={() => {
        Orientation.requestDisableOrientations();
        checkLocked();
      }}
              title={'Disable Screen Orientation'}>
      </Button>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
  box: {
    width: 60,
    height: 60,
    marginVertical: 20,
  },
});
