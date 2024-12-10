package com.sicei.app.sicei.service;

import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.UpdateItemRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanResponse;
import software.amazon.awssdk.services.dynamodb.model.UpdateItemRequest;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class DynamoDbService {

    private final DynamoDbClient dynamoDbClient;

    public DynamoDbService(DynamoDbClient dynamoDbClient) {
        this.dynamoDbClient = dynamoDbClient;
    }

    public void createSession(Long alumnoId, String sessionString) {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put("id", AttributeValue.builder().s(UUID.randomUUID().toString()).build());
        item.put("fecha", AttributeValue.builder().n(String.valueOf(Instant.now().getEpochSecond())).build());
        item.put("alumnoId", AttributeValue.builder().n(String.valueOf(alumnoId)).build());
        item.put("active", AttributeValue.builder().bool(true).build());
        item.put("sessionString", AttributeValue.builder().s(sessionString).build());

        PutItemRequest request = PutItemRequest.builder()
                .tableName("sesiones-alumnos")
                .item(item)
                .build();

        dynamoDbClient.putItem(request);
    }

    public boolean verifySession(Long alumnoId, String sessionString) {
        // Construir el filtro de la operación scan
        Map<String, AttributeValue> expressionValues = new HashMap<>();
        expressionValues.put(":alumnoId", AttributeValue.builder().n(String.valueOf(alumnoId)).build());
        expressionValues.put(":sessionString", AttributeValue.builder().s(sessionString).build());
        expressionValues.put(":active", AttributeValue.builder().bool(true).build());

        // Crear la solicitud de scan
        ScanRequest scanRequest = ScanRequest.builder()
                .tableName("sesiones-alumnos")
                .filterExpression("alumnoId = :alumnoId AND sessionString = :sessionString AND active = :active")
                .expressionAttributeValues(expressionValues)
                .build();

        // Ejecutar la solicitud y procesar la respuesta
        ScanResponse scanResponse = dynamoDbClient.scan(scanRequest);
        return !scanResponse.items().isEmpty(); // Retorna true si se encuentra un ítem que coincide
    }

    public void logoutSession(Long alumnoId, String sessionString) {
        // Construir el filtro de la operación scan
        Map<String, AttributeValue> expressionValues = new HashMap<>();
        expressionValues.put(":alumnoId", AttributeValue.builder().n(String.valueOf(alumnoId)).build());
        expressionValues.put(":sessionString", AttributeValue.builder().s(sessionString).build());

        // Crear la solicitud de scan
        ScanRequest scanRequest = ScanRequest.builder()
                .tableName("sesiones-alumnos")
                .filterExpression("alumnoId = :alumnoId AND sessionString = :sessionString")
                .expressionAttributeValues(expressionValues)
                .build();

        // Ejecutar el scan
        ScanResponse scanResponse = dynamoDbClient.scan(scanRequest);

        // Verificar si se encontró un ítem
        if (scanResponse.items().isEmpty()) {
            throw new RuntimeException("Sesión no encontrada");
        }

        // Obtener la clave primaria del ítem encontrado
        Map<String, AttributeValue> item = scanResponse.items().get(0);
        String id = item.get("id").s(); // Suponiendo que "id" es la Partition Key

        // Actualizar el campo "active" a false
        Map<String, AttributeValue> key = new HashMap<>();
        key.put("id", AttributeValue.builder().s(id).build());

        Map<String, AttributeValue> updateValues = new HashMap<>();
        updateValues.put(":inactive", AttributeValue.builder().bool(false).build());

        UpdateItemRequest updateRequest = UpdateItemRequest.builder()
                .tableName("sesiones-alumnos")
                .key(key)
                .updateExpression("SET active = :inactive")
                .expressionAttributeValues(updateValues)
                .build();

        dynamoDbClient.updateItem(updateRequest);
    }
}
