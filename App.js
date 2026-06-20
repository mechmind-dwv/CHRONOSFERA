import React, { useState, useEffect } from 'react';
import { StyleSheet, Text, View, Dimensions, SafeAreaView } from 'react-native';
import { Magnetometer } from 'expo-sensors';

export default function App() {
  const [data, setData] = useState({ x: 0, y: 0, z: 0 });
  const [subscription, setSubscription] = useState(null);
  const [exposureTime, setExposureTime] = useState(0); // in seconds

  // Simulación de Intensidad RF Base (hasta que haya módulo en fases futuras)
  const baseRF = 10;

  useEffect(() => {
    // Cronómetro de exposición
    const timer = setInterval(() => {
      setExposureTime((prev) => prev + 1);
    }, 1000);

    // Suscripción al magnetómetro de baja frecuencia (1 segundo / 1000ms)
    // Esto evita que la UX se congele por exceso de re-renders
    Magnetometer.setUpdateInterval(1000);
    const sub = Magnetometer.addListener(result => {
      setData(result);
    });
    setSubscription(sub);

    return () => {
      clearInterval(timer);
      sub && sub.remove();
    };
  }, []);

  // Cálculo de la Magnitud Total (Teorema de Pitágoras en 3D)
  const magnitude = Math.sqrt(
    data.x * data.x +
    data.y * data.y +
    data.z * data.z
  );

  // Ecuación del Índice de Carga Disipativa
  const exposureFactor = Math.min((exposureTime / 60), 50); // Capped para la simulación
  const dissipativeIndex = (magnitude * 0.5) + (baseRF * 0.3) + (exposureFactor * 0.2);

  // Lógica de colores por estado termodinámico
  const getStatusColor = (index) => {
    if (index < 25) return '#22C55E'; // Verde: Equilibrio Entrópico
    if (index < 50) return '#F59E0B'; // Naranja: Flujo Activo / Excitabilidad
    return '#EF4444';                 // Rojo: Perturbación / Electropolución
  };

  const statusColor = getStatusColor(dissipativeIndex);

  return (
    <SafeAreaView style={styles.container}>
      <Text style={styles.title}>CHRONOSFERA</Text>
      <Text style={styles.subtitle}>El Pulso de la Noósfera</Text>

      <View style={[styles.circle, { borderColor: statusColor }]}>
        <Text style={[styles.indexValue, { color: statusColor }]}>
          {dissipativeIndex.toFixed(1)}
        </Text>
        <Text style={styles.indexLabel}>Índice de Carga</Text>
      </View>

      <View style={styles.card}>
        <Text style={styles.cardTitle}>Monitor Geomagnético Local</Text>
        <Text style={styles.cardText}>Eje X: {data.x.toFixed(2)} μT</Text>
        <Text style={styles.cardText}>Eje Y: {data.y.toFixed(2)} μT</Text>
        <Text style={styles.cardText}>Eje Z: {data.z.toFixed(2)} μT</Text>
        <Text style={[styles.cardText, { marginTop: 8, fontWeight: 'bold' }]}>
          Magnitud Total (Flujo): {magnitude.toFixed(2)}
        </Text>
      </View>
      
      <Text style={styles.footer}>
        Tiempo de exposición acoplado: {exposureTime} seg
      </Text>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#0F172A',
    alignItems: 'center',
    justifyContent: 'center',
    padding: 24,
  },
  title: {
    fontSize: 28,
    fontWeight: 'bold',
    color: '#38BDF8',
    letterSpacing: 2,
    marginBottom: 4,
  },
  subtitle: {
    fontSize: 14,
    color: '#94A3B8',
    marginBottom: 40,
    textAlign: 'center',
  },
  circle: {
    width: Dimensions.get('window').width * 0.6,
    height: Dimensions.get('window').width * 0.6,
    borderRadius: Dimensions.get('window').width * 0.3,
    borderWidth: 8,
    alignItems: 'center',
    justifyContent: 'center',
    marginBottom: 40,
    backgroundColor: '#1E293B',
  },
  indexValue: {
    fontSize: 56,
    fontWeight: 'bold',
  },
  indexLabel: {
    fontSize: 14,
    color: '#94A3B8',
    marginTop: 8,
    textTransform: 'uppercase',
    letterSpacing: 1,
  },
  card: {
    width: '100%',
    backgroundColor: '#1E293B',
    borderRadius: 16,
    padding: 20,
    alignItems: 'center',
    borderWidth: 1,
    borderColor: '#334155',
  },
  cardTitle: {
    fontSize: 14,
    fontWeight: 'bold',
    color: '#38BDF8',
    marginBottom: 16,
    textTransform: 'uppercase',
    letterSpacing: 1,
  },
  cardText: {
    fontSize: 16,
    color: '#F8FAFC',
    marginVertical: 4,
    fontFamily: 'monospace',
  },
  footer: {
    marginTop: 32,
    fontSize: 12,
    color: '#64748B',
  }
});
