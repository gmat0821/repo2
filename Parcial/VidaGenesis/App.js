import 'react-native-gesture-handler';
import React, { useState, useEffect } from 'react';
import { Animated, View, Text, StyleSheet } from 'react-native';
import { NavigationContainer } from '@react-navigation/native';
import { createDrawerNavigator } from '@react-navigation/drawer';
import { Ionicons } from '@expo/vector-icons';

import HomeScreen from './componentes/HomeScreen';
import AppMemorama from './componentes/AppMemorama';
import AppPropinas from './componentes/AppPropinas';
import TabGroup from './componentes/TabGroup';

const Drawer = createDrawerNavigator();

export default function App() {
  const [isSplashVisible, setSplashVisible] = useState(true);
  const scaleAnim = useState(new Animated.Value(0.5))[0];
  const fadeAnim = useState(new Animated.Value(0))[0];

  useEffect(() => {
    Animated.parallel([
      Animated.spring(scaleAnim, {
        toValue: 1,
        friction: 4,
        useNativeDriver: true,
      }),
      Animated.timing(fadeAnim, {
        toValue: 1,
        duration: 1000,
        useNativeDriver: true,
      })
    ]).start(() => {
      setTimeout(() => {
        Animated.timing(fadeAnim, {
          toValue: 0,
          duration: 500,
          useNativeDriver: true,
        }).start(() => setSplashVisible(false));
      }, 1500);
    });
  }, []);

  if (isSplashVisible) {
    return (
      <View style={styles.splashContainer}>
        <Animated.View style={{ opacity: fadeAnim, transform: [{ scale: scaleAnim }], alignItems: 'center' }}>
          <Ionicons name="hardware-chip" size={120} color="#00E5FF" />
          <Text style={styles.splashText}>VidaGenesis11</Text>
        </Animated.View>
      </View>
    );
  }

  return (
    <NavigationContainer>
      <Drawer.Navigator
        initialRouteName="Inicio"
        screenOptions={{
          headerStyle: { backgroundColor: '#121212', shadowColor: 'transparent', elevation: 0 },
          headerTintColor: '#00E5FF',
          drawerActiveTintColor: '#00E5FF',
          drawerInactiveTintColor: '#A0A0A0',
          drawerStyle: { backgroundColor: '#1A1A1A' },
          sceneContainerStyle: { backgroundColor: '#121212' }
        }}
      >
        <Drawer.Screen name="Inicio" component={HomeScreen} options={{ drawerIcon: ({color}) => <Ionicons name="planet" size={22} color={color} /> }} />
        <Drawer.Screen name="Memorama" component={AppMemorama} options={{ drawerIcon: ({color}) => <Ionicons name="apps" size={22} color={color} /> }} />
        <Drawer.Screen name="Propinas" component={AppPropinas} options={{ drawerIcon: ({color}) => <Ionicons name="wallet" size={22} color={color} /> }} />
        <Drawer.Screen name="Zona Arcade" component={TabGroup} options={{ drawerIcon: ({color}) => <Ionicons name="game-controller-outline" size={22} color={color} /> }} />
      </Drawer.Navigator>
    </NavigationContainer>
  );
}

const styles = StyleSheet.create({
  splashContainer: {
    flex: 1,
    backgroundColor: '#0A0A0A',
    justifyContent: 'center',
    alignItems: 'center',
  },
  splashText: {
    color: '#00E5FF',
    fontSize: 36,
    fontWeight: '900',
    marginTop: 20,
    textShadowColor: 'rgba(0, 229, 255, 0.5)',
    textShadowOffset: { width: 0, height: 0 },
    textShadowRadius: 10,
  }
});