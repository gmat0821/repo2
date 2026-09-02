import { StatusBar } from 'expo-status-bar';
import { StyleSheet, Text, View } from 'react-native';
import { NavigationContainer } from '@react-navigation/native';
import { createNativeStackNavigator } from '@react-navigation/native-stack';
import HomeScreen from './SRC/Screens/HomeScreen';
import ImcScreen from './SRC/Screens/ImcScreen';
import CurrencyScreen from './SRC/Screens/CurrencyScreen';
import TipScreen from './SRC/Screens/TipScreen';

const Stack = createNativeStackNavigator();

export default function App() {
  return (
    <NavigationContainer>
      <Stack.Navigator initialRouteName="Home">
        <Stack.Screen name="Home" component={HomeScreen}  options ={{ title: 'Menú Principal' }}/>
        <Stack.Screen name="IMC" component={ImcScreen} options ={{ title: 'Calculadora IMC' }} />
        <Stack.Screen name="Currency" component={CurrencyScreen} options ={{ title: 'Conversor de Divisas' }} />
        <Stack.Screen name="Tip" component={TipScreen} options ={{ title: 'Calculadora de Propinas' }} />
      </Stack.Navigator>
    </NavigationContainer>
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
