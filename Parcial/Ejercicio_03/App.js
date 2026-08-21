import { StatusBar } from 'expo-status-bar';
import { StyleSheet, Text, View } from 'react-native';
import ImagenFondo from './Componentes/ImagenFondo';
import DemoFlatList from './Componentes/DemoFlatList';
import DemoSection from './Componentes/DemoSection';

export default function App() {
  return (
    <View style={styles.container}>
      <DemoFlatList />      
   
     

      <StatusBar style="auto" />
    </View>
  );s
}


const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#fff',
    alignItems: 'center',
    justifyContent: 'center',
  },
});



