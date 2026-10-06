// App.js
import React from 'react';
import { NavigationContainer } from '@react-navigation/native';
import { createNativeStackNavigator } from '@react-navigation/native-stack';
import { useFonts } from 'expo-font';
import { Inter_400Regular } from '@expo-google-fonts/inter';
import { SpaceGrotesk_700Bold } from '@expo-google-fonts/space-grotesk';
import { SpaceMono_700Bold } from '@expo-google-fonts/space-mono';

import CalendarioPrincipal from './componentes/calendarioPrincipal'; 
import ResumenDelDia from './componentes/resumenDelDia';
import ProximaCita from './componentes/proximaCita';

const Stack = createNativeStackNavigator();

export default function App() {
  const [fontsLoaded] = useFonts({
    Inter: Inter_400Regular,
    SpaceGrotesk: SpaceGrotesk_700Bold,
    SpaceMono: SpaceMono_700Bold,
  });

  if (!fontsLoaded) {
    return null;
  }

  return (
    <NavigationContainer>
      {/* headerShown: false oculta la barra superior por defecto de la navegación */}
      <Stack.Navigator screenOptions={{ headerShown: false }}>
        <Stack.Screen name="Calendario" component={CalendarioPrincipal} />
        <Stack.Screen name="ResumenDelDia" component={ResumenDelDia} />
        <Stack.Screen name="ProximaCita" component={ProximaCita} />
      </Stack.Navigator>
    </NavigationContainer>
  );
}