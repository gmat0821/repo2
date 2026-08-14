import { StatusBar } from 'expo-status-bar';
import { StyleSheet, Text, View } from 'react-native';
import Micomponente from './componentes/Micomponente';
import Mensaje from './componentes/mensaje';


export default function App() {
  return (
    <View style={styles.container}>
      <Mensaje titulo="Hola desde una propiedad" numero="25" />
      <Mensaje titulo="Bienvenido al curso React Native" numero="30" />
      <Micomponente />
      <Text>Open up App.js to start working on your app!</Text>
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
