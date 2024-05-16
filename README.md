# React Native Orientation
[![](https://img.shields.io/badge/yarn-v1.0.0-blue)](https://www.npmjs.com/package/rn-detect-orientation)
[![](https://img.shields.io/badge/native_language-Kotlin_&_Swift-green)](https://www.npmjs.com/package/rn-detect-orientation)
[![](https://img.shields.io/badge/size-35.2_kB-red)](https://www.npmjs.com/package/rn-detect-orientation)
[![](https://img.shields.io/badge/languag_kolin-32.9%_coverage-blue)](https://www.npmjs.com/package/rn-detect-orientation)
[![](https://img.shields.io/badge/languag_swift-18.4%_coverage-green)](https://www.npmjs.com/package/rn-detect-orientation)
[![](https://img.shields.io/badge/languag_typescript-15.4%_coverage-red)](https://www.npmjs.com/package/rn-detect-orientation)

Curently, works on Android and IOS

### 🚀 Installation

```sh
npm install rn-detect-orientation
```

or

```sh
yarn add rn-detect-orientation
```

### 🚀 Usage

```tsx
import { useEffect, useState } from 'react';
import { StyleSheet, View, Text, Button } from 'react-native';
import Orientation from 'rn-detect-orientation';

export default function App() {
  const [result, setResult] = useState<number | undefined>();
  const [orient, setOrient] = useState<string>();
  const [isLocked, setLocked] = useState<boolean>(false);
  const [sendValue, setValue] = useState<string | null>(null);

  useEffect(() => {
    Orientation.addOrientationListener(onOrientationDidUpdate);
    return () => {
      Orientation.removeOrientationListener();
    };
  }, []);

  const onOrientationDidUpdate = (event) => {
    setOrient(`${event}`);
  };

  useEffect(() => {
    let initialOrientation = Orientation.getInitialOrientation();
    setOrient(`${initialOrientation}`);
  }, []);

  const checkLocked = () => {
    const locked = Orientation.isLocked();
    if (locked !== isLocked) {
      setLocked(locked);
    }
  };

  return (
    <View style={{
      flex: 1,
      alignItems: 'center',
      justifyContent: 'center',
    }}>
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
```

### 🚀 Event
```
- addOrientationListener: (cb: any) => import("react-native").EmitterSubscription;
- removeOrientationListener: () => void;
```

### 🚀 Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

### 🚀 License
    MIT License
    Copyright (c) 2024 Forest Nguyen
---
