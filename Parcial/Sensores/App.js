import { StatusBar } from 'expo-status-bar';
import { StyleSheet, Text, View } from 'react-native';
import AccelerometerSensor from './componentes/AccelerometerSensor';
import GyroscopeSensor from './componentes/gyroscopeSensor';
import GyroscopeSensor2 from './componentes/gyroscopeSensor2';
import MagnetometroSensor from './componentes/magnetometroSensor';

export default function App() {
  return (
    <View style={styles.container}>
      <MagnetometroSensor />
      <StatusBar style="auto" />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#fff',
    alignItems: 'center',
    justifyContent: 'center',
  },
});
