import React, { useState } from 'react';
import { NavigationContainer } from '@react-navigation/native';
import { createBottomTabNavigator } from '@react-navigation/bottom-tabs';
import { Ionicons } from '@expo/vector-icons'; // Importamos los íconos de Expo

import MazosScreen from './src/Screens/MazosScreen';
import EstudioScreen from './src/Screens/EstudioScreen';

const Tab = createBottomTabNavigator();

export default function App() {
  const [mazos, setMazos] = useState([
    {
      id: 'm1',
      nombre: 'Gestión de Proyectos',
      tarjetas: [
        { id: 't1', pregunta: '¿Qué es el OPM?', respuesta: 'Organizational Project Management' }
      ]
    }
  ]);

  return (
    <NavigationContainer>
      <Tab.Navigator
        // screenOptions ahora recibe "route" para saber en qué pestaña estamos
        screenOptions={({ route }) => ({
          tabBarIcon: ({ focused, color, size }) => {
            let iconName;

            // Asignamos un ícono dependiendo del nombre de la pestaña
            if (route.name === 'Mis Mazos') {
              // Si está seleccionada (focused), usamos el ícono relleno, si no, el de contorno
              iconName = focused ? 'albums' : 'albums-outline';
            } else if (route.name === 'Estudiar') {
              iconName = focused ? 'game-controller' : 'game-controller-outline';
            }

            // Retornamos el componente del ícono
            return <Ionicons name={iconName} size={size + 4} color={color} />;
          },
          tabBarActiveTintColor: '#BA5A5A', // Color cuando la pestaña está seleccionada
          tabBarInactiveTintColor: '#86BCBD', // Color cuando no está seleccionada
          headerStyle: { backgroundColor: '#86BCBD' },
          headerTintColor: '#fff',
          headerTitleStyle: { fontWeight: 'bold', fontSize: 22 },
          
          tabBarStyle: { 
            backgroundColor: '#ffffff', 
            height: 70,             // Altura equilibrada
            paddingBottom: 10,      // Reducimos este valor para no aplastar los íconos hacia abajo
            paddingTop: 10,         // Un poco de aire arriba
            borderTopWidth: 2,      // Línea decorativa
            borderTopColor: '#F7E49B'
          },
          tabBarLabelStyle: {
            fontSize: 13,
            fontWeight: 'bold',
            marginBottom: 5         // Empuja el texto ligeramente hacia arriba para separarlo del borde
          }
        })}
      >
        <Tab.Screen 
          name="Mis Mazos" 
          options={{ title: "Mis Mazos 📚" }} // El title es para el texto de arriba, el name para la pestaña
        >
          {() => <MazosScreen mazos={mazos} setMazos={setMazos} />}
        </Tab.Screen>
        
        <Tab.Screen 
          name="Estudiar" 
          options={{ title: "Estudiar 🚀" }}
        >
          {() => <EstudioScreen mazos={mazos} />}
        </Tab.Screen>
      </Tab.Navigator>
    </NavigationContainer>
  );
}