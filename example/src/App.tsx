import * as React from 'react';

import { StyleSheet, View, Text } from 'react-native';
import Orientation, { multiply } from 'rn-orientation';

export default function App() {
  const [result, setResult] = React.useState<number | undefined>();
  const [orient, setOrient] = React.useState<string>();

  React.useEffect(() => {
    multiply(3, 7).then(setResult);
  }, []);

  React.useEffect(() => {
    let initialOrientation = Orientation.getInitialOrientation();
    setOrient(`${initialOrientation}`);
  }, []);

  return (
    <View style={styles.container}>
      <Text>Result: {result}</Text>
      <Text>Result: {orient}</Text>
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
