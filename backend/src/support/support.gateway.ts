import { SubscribeMessage, WebSocketGateway, OnGatewayInit, OnGatewayConnection, OnGatewayDisconnect, WebSocketServer, MessageBody, ConnectedSocket } from '@nestjs/websockets';
import { Logger } from '@nestjs/common';
import { Server, Socket } from 'socket.io';
import { SupportService } from './support.service';
import { SendChatMessageDto } from './support.dto';

@WebSocketGateway({
  cors: {
    origin: '*',
  },
  namespace: 'support-chat',
})
export class SupportGateway implements OnGatewayInit, OnGatewayConnection, OnGatewayDisconnect {
  @WebSocketServer() server: Server;
  private readonly logger = new Logger('SupportGateway');

  constructor(private readonly supportService: SupportService) {}

  afterInit(server: Server) {
    this.logger.log('Live Chat WebSocket Gateway initialized successfully!');
  }

  handleConnection(client: Socket, ...args: any[]) {
    this.logger.log(`Client connected: ${client.id}`);
  }

  handleDisconnect(client: Socket) {
    this.logger.log(`Client disconnected: ${client.id}`);
  }

  @SubscribeMessage('joinSession')
  handleJoinRoom(@ConnectedSocket() client: Socket, @MessageBody() data: { sessionId: string }) {
    client.join(data.sessionId);
    this.logger.log(`Socket Client ${client.id} joined Chat Room Session: ${data.sessionId}`);
    client.emit('roomJoined', { status: 'SUCCESS', message: `Connected to session ${data.sessionId}` });
  }

  @SubscribeMessage('sendMessage')
  async handleMessage(
    @ConnectedSocket() client: Socket,
    @MessageBody() data: { sessionId: string; dto: SendChatMessageDto }
  ) {
    this.logger.log(`Websocket Message in room: ${data.sessionId}`);
    const updatedSession = await this.supportService.sendChatMessage(data.sessionId, data.dto);
    
    // Broadcast message to everyone in the room session
    this.server.to(data.sessionId).emit('newMessage', {
      sessionId: data.sessionId,
      message: updatedSession.messages[updatedSession.messages.length - 1],
    });
  }

  @SubscribeMessage('typingIndicator')
  handleTyping(
    @ConnectedSocket() client: Socket,
    @MessageBody() data: { sessionId: string; senderName: string; isTyping: boolean }
  ) {
    // Broadcast typing status to everyone else in the room session
    client.to(data.sessionId).emit('typingStatus', {
      senderName: data.senderName,
      isTyping: data.isTyping,
    });
  }
}
