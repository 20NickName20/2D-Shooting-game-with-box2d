package io.github._20nickname20.imbored.screens;

import com.badlogic.gdx.*;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerAdapter;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import io.github._20nickname20.imbored.ControlsProfile;
import io.github._20nickname20.imbored.Main;
import io.github._20nickname20.imbored.net.Network;
import io.github._20nickname20.imbored.util.Util;
import io.github._20nickname20.imbored.world.ClientWorld;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Set;

import static io.github._20nickname20.imbored.Main.inputMultiplexer;

public class ConnectionScreen extends ScreenAdapter {
    private final SpriteBatch batch;
    private final BitmapFont font;
    private final FitViewport viewport;
    OrthographicCamera camera;

    private final Main game;

    private int selectedButton;

    String ip = "";
    boolean invalidIp;

    List<ControlsProfile> profiles;

    ClientWorld clientWorld = null;

    List<Button> buttons = List.of(
        new Button("1", Color.GREEN) {
            @Override
            public void onUse() {
                if (ip.length() >= 15) return;
                ip += "1";
            }
        },
        new Button("2", Color.GREEN) {
            @Override
            public void onUse() {
                if (ip.length() >= 15) return;
                ip += "2";
            }
        },
        new Button("3", Color.GREEN) {
            @Override
            public void onUse() {
                if (ip.length() >= 15) return;
                ip += "3";
            }
        },
        new Button("4", Color.GREEN) {
            @Override
            public void onUse() {
                if (ip.length() >= 15) return;
                ip += "4";
            }
        },
        new Button("5", Color.GREEN) {
            @Override
            public void onUse() {
                if (ip.length() >= 15) return;
                ip += "5";
            }
        },
        new Button("6", Color.GREEN) {
            @Override
            public void onUse() {
                if (ip.length() >= 15) return;
                ip += "6";
            }
        },
        new Button("7", Color.GREEN) {
            @Override
            public void onUse() {
                if (ip.length() >= 15) return;
                ip += "7";
            }
        },
        new Button("8", Color.GREEN) {
            @Override
            public void onUse() {
                if (ip.length() >= 15) return;
                ip += "8";
            }
        },
        new Button("9", Color.GREEN) {
            @Override
            public void onUse() {
                if (ip.length() >= 15) return;
                ip += "9";
            }
        },
        new Button("0", Color.GREEN) {
            @Override
            public void onUse() {
                if (ip.length() >= 15) return;
                ip += "0";
            }
        },
        new Button(".", Color.GREEN) {
            @Override
            public void onUse() {
                if (ip.length() >= 15) return;
                ip += ".";
            }
        }
    );

    void resetButtons() {
        for (Button button : buttons) {
            button.reset();
        }
    }

    InputProcessor menuInputProcessor() {
        return new InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                buttons.get(selectedButton).onUse();
                return false;
            }

            private final Set<Integer> continueKeys = Set.of(Input.Keys.SPACE);

            @Override
            public boolean keyDown(int keycode) {
                resetButtons();

                if (continueKeys.contains(keycode)) {
                    buttons.get(selectedButton).onUse();
                    return false;
                }

                if (keycode == Input.Keys.ENTER) {
                    if (ip.isEmpty()) {
                        new Thread(() -> {
                            invalidIp = true;
                            ClientWorld attemptClientWorld = new ClientWorld(profiles);

                            if (!attemptClientWorld.hostNotFound) {
                                clientWorld = attemptClientWorld;
                            }
                        }).start();

                    } else if (Network.isValidIPv4(ip)) {
                        new Thread(() -> {
                            try {
                                InetAddress ipAddress = InetAddress.getByName(ip);
                                ClientWorld attemptClientWorld = new ClientWorld(profiles, ipAddress);
                                if (!attemptClientWorld.hostNotFound) {
                                    clientWorld = attemptClientWorld;
                                }
                            } catch (UnknownHostException e) {
                                ip = "";
                                batch.begin();
                                font.setColor(Color.BLACK);
                                font.draw(batch, "Something went wrong", 7, 4);
                                batch.end();
                            }
                        }).start();
                    } else {
                        invalidIp = true;
                        invalidIpTime = Util.time();
                    }
                }

                if (keycode == Input.Keys.BACKSPACE) {
                    if (ip.isEmpty()) return false;
                    ip = ip.substring(0, ip.length() - 1);
                }

                if (keycode == Input.Keys.ESCAPE) {
                    game.setScreen(new MainMenuScreen(game));
                }

                if (keycode == Input.Keys.DOWN) {
                    selectedButton += 1;
                }
                if (keycode == Input.Keys.UP) {
                    selectedButton -= 1;
                }

                if (selectedButton < 0) {
                    selectedButton = buttons.size() - 1;
                }
                if (selectedButton >= buttons.size()) {
                    selectedButton = 0;
                }
                return false;
            }

            @Override
            public boolean keyTyped(char character) {
                if (ip.length() >= 15) return false;
                if (Character.isDigit(character) || character == '.') {
                    ip += character;
                }
                return false;
            }
        };
    }

    public ConnectionScreen(Main game, List<ControlsProfile> profiles) {
        this.game = game;
        this.profiles = profiles;

        inputMultiplexer.clear();
        inputMultiplexer.addProcessor(menuInputProcessor());

        batch = new SpriteBatch();

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/PressStart2P-Regular.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.characters += "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯабвгдеёжзийклмнопрстуфхцчшщъыьэюя";
        parameter.size = 27;
        font = generator.generateFont(parameter);
        generator.dispose();


        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        viewport = new FitViewport(16, 9, camera);
        viewport.apply();
        camera.update();

        font.setUseIntegerPositions(false);
        font.getData().setScale(viewport.getWorldHeight() / Gdx.graphics.getHeight());

        ControllerListener listener = new ControllerAdapter() {
            @Override
            public boolean buttonUp(Controller controller, int buttonIndex) {
                buttons.get(selectedButton).onUse();
                return false;
            }
        };
        Controllers.addListener(listener);
    }

    private float invalidIpTime;

    @Override
    public void render(float dt) {
        ScreenUtils.clear(new Color(1f, 1f, 1f, 1));

        batch.setProjectionMatrix(viewport.getCamera().combined);

        batch.begin();
        font.setColor(Color.BLACK);
        font.draw(batch, "Введите ip: ", 7, 8);
        font.draw(batch, ip, 7, 7);
        font.draw(batch, "Чтобы присоединиться к локальному хосту то оставьте поле пустым", 0.25f, 3);
        font.draw(batch, "Нажмите Enter чтобы присоединиться", 4, 2);

        for (int i = 0; i < buttons.size(); i++) {
            String text = buttons.get(i).text;
            if (selectedButton == i) {
                text = "->" + text + "<-";
                font.draw(batch, text, i * 1.5f, 5f);
                continue;
            }
            font.draw(batch, text, 0.5f + i * 1.5f, 5f);
        }

        if (Util.time() - invalidIpTime < 5 && invalidIp) {
            font.setColor(Color.RED);
            font.draw(batch, "Wrong ip", 7, 4);
        } else {
            invalidIp = false;
        }


        batch.end();

        if (clientWorld != null) {
            game.setScreen(new GameScreen(game, clientWorld));
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
    }


}
