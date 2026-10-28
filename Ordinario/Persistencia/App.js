import React from 'react';
import { NavigationContainer } from '@react-navigation/native';
import { createBottomTabNavigator } from '@react-navigation/bottom-tabs';
import { Ionicons } from '@expo/vector-icons';

import AgregarScreen from './src/screens/AgregarScreen';
import ConsultarScreen from './src/screens/ConsultarScreen';
import ActualizarScreen from './src/screens/ActualizarScreen';
import EliminarScreen from './src/screens/EliminarScreen';

const Tab = createBottomTabNavigator();

export default function App() {
  return (
    <NavigationContainer>
      <Tab.Navigator 
        screenOptions={{
          headerStyle: { backgroundColor: '#1e293b' },
          headerTitleStyle: { color: '#f8fafc', fontWeight: 'bold' },
          tabBarStyle: { backgroundColor: '#1e293b', borderTopColor: '#334155' },
          tabBarActiveTintColor: '#6366f1',
          tabBarInactiveTintColor: '#64748b',
        }}
      >
        <Tab.Screen 
          name="Nuevo" 
          component={AgregarScreen} 
          options={{ 
            title: 'Agregar',
            tabBarIcon: ({color, size}) => <Ionicons name="add-circle" size={size} color={color}/> 
          }}
        />
        <Tab.Screen 
          name="Lista" 
          component={ConsultarScreen} 
          options={{ 
            title: 'Consultar',
            tabBarIcon: ({color, size}) => <Ionicons name="grid" size={size} color={color}/> 
          }}
        />
        <Tab.Screen 
          name="Editar" 
          component={ActualizarScreen} 
          options={{ 
            title: 'Actualizar',
            tabBarIcon: ({color, size}) => <Ionicons name="create" size={size} color={color}/> 
          }}
        />
        <Tab.Screen 
          name="Borrar" 
          component={EliminarScreen} 
          options={{ 
            title: 'Eliminar',
            tabBarIcon: ({color, size}) => <Ionicons name="trash" size={size} color={color}/> 
          }}
        />
      </Tab.Navigator>
    </NavigationContainer>
  );
}